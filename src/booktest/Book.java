package translateit2.persistence.booktest;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Book extends Work implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String publisher;
	
	public Book() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Book(Long id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
}
