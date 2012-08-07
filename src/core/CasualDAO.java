package core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Henrico Brum
 */
public class CasualDAO {

    private Connection connection;

    public CasualDAO() {
        //Método Vazio
    }

    public CasualDAO(Connection connection) {
        this.connection = connection;
    }

    /*	Função de Inserção de Pessoa no Banco
     * 
     * Confere se a pessoa já existe no Banco, em caso positivo retorna seu ID para posterior inserção de relacionamento
     * em caso negativo insere a pessoa no Banco e retorna o ID para a mesma finalidade posterior.
     * 
     */
    public int inserePessoa(Person pessoa) {

        //Strings de inserção e busca no banco
        String sqlCodeInsert = "insert into person (name) values (?)";
        String sqlCodeSearch = "select * from person where name = ?";

        int personId = 0;

        //Conexão com Banco
        //Connection dataBase = new ConnectionFactory().getConnection();

        try {
            //Busca da pessoa no Banco
            //PreparedStatement stmt = dataBase.prepareStatement(sqlCodeSearch);
            PreparedStatement stmt = connection.prepareStatement(sqlCodeSearch);

            stmt.setString(1, pessoa.getName());

            ResultSet result = stmt.executeQuery();

            //Caso ela já esteja no Banco é retornado seu ID
            if (result.next()) {

                personId = result.getInt("id_person");

            } else {
                //Inserção de Pessoa no Banco
                //stmt = dataBase.prepareStatement(sqlCodeInsert);
                stmt = connection.prepareStatement(sqlCodeInsert);
                stmt.setString(1, pessoa.getName());
                stmt.execute();

                //Busca pela ID gerada
                //stmt = dataBase.prepareStatement(sqlCodeSearch);
                stmt = connection.prepareStatement(sqlCodeSearch);
                stmt.setString(1, pessoa.getName());
                result = stmt.executeQuery();
                result.next();

                personId = result.getInt("id_person");

            }

            //Encerra conexões com o Banco
            result.close();
            stmt.close();
            //dataBase.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return personId;
    }

    /*	Função de Inserção de Relacionamento no Banco
     * 
     * Calcula a distância entre duas pessoas na página e confere se já foi inserido um relacionamento entre elas nessa
     * página. Em caso positivo, confere se a distância é inferior à recentemente calculada e atualiza o Banco.
     * Em caso negativo, insere o relacionamento no Banco com a distancia calculada.
     * 
     */
    public void insereRelacionamento(Person p1, Person p2, int id1, int id2, String url) {

        //Distância do Relacionamento
        int distancia = p1.getDistanceOf(p2);

        //Strings de atualização e busca no Banco
        String sqlUpdateRelation = "update relationship set distance = ? where id_relationship = ?";
        String sqlSearchRelation = "select * from relationship where id_person1 = ? AND id_person2 = ? AND relationship_link = ?";

        //Conexão com o Banco
        //Connection dataBase = new ConnectionFactory().getConnection();

        try {

            //Busca pelo relacionamento no Banco
            //PreparedStatement stmt = dataBase.prepareStatement(sqlSearchRelation);
            PreparedStatement stmt = connection.prepareStatement(sqlSearchRelation);
            stmt.setString(1, Integer.toString(id1));
            stmt.setString(2, Integer.toString(id2));
            stmt.setString(3, url);

            ResultSet result = stmt.executeQuery();

            //Caso exista o relacionamento no banco
            if (result.next()) {

                //Procedimento de atualização de distância
                if (result.getInt("distance") > distancia) {
                    //stmt = dataBase.prepareStatement(sqlUpdateRelation);
                    stmt = connection.prepareStatement(sqlUpdateRelation);
                    stmt.setString(1, Integer.toString(distancia));
                    stmt.setString(2, Integer.toString(result.getInt("id_relationship")));

                    stmt.execute();

                }

                //Procedimento de inserção de relacionamento
            } else {
                //String de inserção no Banco
                String sqlInsertRelation = "insert into relationship (id_person1, id_person2, relationship_link, distance) values (";
                sqlInsertRelation += (id1 + " , " + id2 + " , '" + url + "' , " + distancia + ")");

                //stmt = dataBase.prepareStatement(sqlInsertRelation);
                stmt = connection.prepareStatement(sqlInsertRelation);

                stmt.execute();
            }

            //Encerra conexão com Banco
            result.close();
            stmt.close();
            //dataBase.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}