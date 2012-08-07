/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class MyCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private final static Pattern PALAVRA_CHAVE = Pattern.compile(".*alegrete.*", Pattern.CASE_INSENSITIVE);

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && (href.startsWith("http://pt.wikipedia.org/"));
    }

    /**
     * This function implements some tests on a word from the text to figure out
     * if it's a person or not.
     */
    public boolean isPerson(String word) {
        int i;

        //  Teste referente ao tamanho da palavra, excluindo palavras com menos
        // de 3 caracteres e mais de 10.
        //
        if (word.length() < 3 || word.length() > 10) {
            return false;
        }

        //  Teste referente a primeira letra da palavra ser maiúscula, indicando
        // nome de pessoa em caso geral.
        //
        if (word.charAt(0) != 'A') {
            return false;
        }

        //  Teste de ocorrência de letra maiúscula no restante da palavra, indicando
        // não se tratar de um nome próprio e sim de uma sigla de Organização ou falha
        // ocorrente na palavra
        //
        for (i = 1; i < word.length(); i++) {
            if (Character.isUpperCase(word.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * This function 'cleans' a word, excluding dots, commas or any kind of
     * grammar alpha character.
     */
    public String cleanWord(String word) {

        int i;
        String newWord = "";

        for (i = 0; i < word.length(); i++) {

            if (Character.isLetter(word.charAt(i))) {
                newWord += word.charAt(i);
            }
        }

        return newWord;
    }

    /**
     * This function adds a Person to a Vector or increases the Offset Vector of
     * a Person.
     */
    public Vector<Person> inserPerson(String name, int offset, Vector<Person> people) {

        for (int i = 0; i < people.size(); i++) {
            if (people.elementAt(i).getName().equals(name)) {
                people.elementAt(i).addOffset(offset);
                return people;
            }
        }

        people.add(new Person(name, offset));

        return people;
    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            List<WebURL> links = htmlParseData.getOutgoingUrls();

            //System.out.println("Text length: " + text.length());
            //System.out.println("Html length: " + html.length());
            //System.out.println("Number of outgoing links: " + links.size());

            //Divisão do texto rastreado da página
            String word[] = text.split(" ");

            //Declaração do vetor de Pessoas encontradas na página
            Vector<Person> person = new Vector<Person>();

            //Definição inicial do offset
            int offset = 0;

            //Busca por Entidades Nomeadas (Em fase de implementação. Palavras iniciadas por 'A'.)
            for (int i = 0; i < word.length; i++) {

                String rawWord = cleanWord(word[i]);

                if (isPerson(rawWord)) {

                    person = inserPerson(rawWord, offset, person);

                }

                offset += (word[i].length() + 1);

            }


            //Objeto de Conexão com banco
            Connection connection = new ConnectionFactory().getConnection();
            CasualDAO sqlInject = new CasualDAO(connection);

            int id1, id2;

            //Procedimentos de Gravação no Banco
            for (Person p1 : person) {

                for (Person p2 : person) {

                    if (p1.getName() != p2.getName()) {
                        //Inserção de pessoas
                        id1 = sqlInject.inserePessoa(p1);
                        id2 = sqlInject.inserePessoa(p2);
                        //Inserção de Relacionamento
                        sqlInject.insereRelacionamento(p1, p2, id1, id2, url);
                    }

                }
            }
            try {
                connection.close();
            } catch (SQLException ex) {
                Logger.getLogger(MyCrawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}