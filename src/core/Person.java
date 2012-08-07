package core;

import java.util.Vector;

public class Person {
	
	//Attributes
	String name;
	Vector<Integer> offset;
	
	//Constructor
	public Person(String name, int offset){
		
		this.name = name;
		this.offset = new Vector<Integer>();
		this.offset.add(offset);
		
	}
	
	//Methods
	
	//Retorno de Nome
	public String getName(){
		return this.name;
	}
	
	//Adição de Offset
	public void addOffset(int offset){
		this.offset.add(offset);
	}
	
	//Função Auxiliar de Cálculo de Distância entre duas Pessoas
	public int getDistanceOf(Person comparePerson){
		int lessDistance = Integer.MAX_VALUE;
		
		for(int n1 : this.offset){
			for(int n2 : comparePerson.offset){
				int distance = n1 - n2;
				
				if(distance < 0)
					distance *= -1;
				
				if(lessDistance > distance){
					lessDistance = distance;
				}
			}
		}
		
		return lessDistance;
		
	}
	
	//Função Auxiliar "print" ~ Imprime uma pessoa (uso em testes)
	public void print(){
		System.out.print("Pessoa "+this.name + " Offset");
		if(this.offset.size() == 1){
			System.out.println(" "+offset.elementAt(0));
			return;
		}
		System.out.print("s ");
		for (int i = 0; i < this.offset.size(); i++) {
			System.out.print(offset.elementAt(i)+" ");
		}
		System.out.println("");
		return;
	}

}
