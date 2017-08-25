package translateit2.persistence.booktest;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class EBook extends Work implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String readerType;
	
	public EBook() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public EBook(Long id, String name) {
		super(id, name);
		// TODO Auto-generated constructor stub
	}

	public String getReaderType() {
		return readerType;
	}

	public void setReaderType(String readerType) {
		this.readerType = readerType;
	}
	
}