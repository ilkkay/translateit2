package translateit2.persistence.booktest;

public interface BookService {
	public Book createBook(final Book entity);
	public EBook createEBook(final EBook entity);
	public Edition createEdition(final Edition entity);
	public Page createPage(final Page entity);
}
