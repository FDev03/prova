// Classe ausiliaria per memorizzare i consigli personalizzati
package book_recommender.lab_b;
public class RecommendedBook {
    public String userId;
    public String bookTitle;
    public String authorName;
    public String category;

    public RecommendedBook(String userId, String bookTitle) {
        this.userId = userId;
        this.bookTitle = bookTitle;
        this.authorName = "";
        this.category = "";
    }
}