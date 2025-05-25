package ru.tbank.bookit.book_it_backend.DTO;

public class UpdateProfileRequest {
    private String name;
    private String email;
    private Long phone;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Long getPhone() { return phone; }
    public void setPhone(Long phone) { this.phone = phone; }
}