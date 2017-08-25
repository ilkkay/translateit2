package translateit2.persistence.booktest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// http://stackoverflow.com/questions/32464680/which-entities-do-i-need-to-create-spring-data-repositories-for
// http://stackoverflow.com/questions/30718798/is-it-necessary-to-create-an-repository-and-a-service-for-each-entity
// https://xenoterracide.com/post/single-repository-one-aggregate/
// project => translatable version (translation units) => localized object (translation units)
// https://github.com/olivergierke/spring-restbucks/tree/master/src/main/java/org/springsource/restbucks/order
@Service
public class BookServiceImpl implements BookService{
	@Autowired
	private BookRepository bookRepo;
	@Autowired
	private EBookRepository ebookRepo;
	@Autowired
	private EditionRepository editionRepo;
	@Autowired
	private PageRepository pageRepo;
	
	@Override
	public Book createBook(final Book entity) {
		Book perBook = bookRepo.save(entity);
		return perBook; 
	}

	@Override
	public Edition createEdition(Edition entity) {
		Edition perEdition = editionRepo.save(entity);
		return perEdition; 
	}

	@Override
	public Page createPage(Page entity) {
		Page perPage = pageRepo.save(entity);
		return perPage; 
	}

	@Override
	public EBook createEBook(EBook entity) {
		EBook perEBook = ebookRepo.save(entity);
		return perEBook; 
	}
	
}
