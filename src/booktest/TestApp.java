package translateit2.persistence.booktest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import translateit2.TranslateIt2v4Application;
import translateit2.persistence.dao.LocoRepository;
import translateit2.service.TransuServiceImpl;

@SpringBootApplication 
public class TestApp {
	@Autowired
    private BookService bookService;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(TestApp.class, args);
	}
	
	public void createBook() {
		Book book = new Book();
		book.setName("My new book");
		book.setPublisher("WSOY");
		book=bookService.createBook(book);
		
		EBook ebook = new EBook();
		ebook.setReaderType("Kindle");
		ebook.setName("Electric sheep");
		ebook=bookService.createEBook(ebook);
		
		Edition edition1= new Edition();
		edition1.setEditionNumber(1);
		edition1.setBook(book);
		edition1=bookService.createEdition(edition1);
		
		Page page1= new Page();
		page1.setPageNumber(1);
		page1.setEdition(edition1);
		page1=bookService.createPage(page1);
		
		Page page2= new Page();
		page2.setPageNumber(2);
		page2.setEdition(edition1);
		page2=bookService.createPage(page2);
		
		Edition edition2= new Edition();
		edition2.setEditionNumber(2);
		edition2.setBook(book);
		edition2=bookService.createEdition(edition2);
		
	}
	
	@Bean
	public CommandLineRunner demo(BookServiceImpl bookService) {
		return (args) -> {	
			createBook();
		};
	}
}
