package vn.bachhoa.dto.request;

public class CreateReviewRequest {
    private int rating;
    private String title;
    private String comment;

    // Getters & Setters
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
