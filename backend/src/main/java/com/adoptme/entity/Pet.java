package com.adoptme.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pets")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer age;

    @Column(nullable = false)
    private String type;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String contactNumber;

    private String location;

    private String landmark;

    @Column(length = 1000)
    private String locationLink;

    private String vaccinationStatus;

    private Boolean stray;

    @Enumerated(EnumType.STRING)
    private AdoptionStatus adoptionStatus;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pet_images", joinColumns = @JoinColumn(name = "pet_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listed_by", nullable = false)
    private User listedBy;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (adoptionStatus == null) {
            adoptionStatus = AdoptionStatus.AVAILABLE;
        }
        if (stray == null) {
            stray = false;
        }
        if (vaccinationStatus == null || vaccinationStatus.isBlank()) {
            vaccinationStatus = "Unknown";
        }
    }

    public enum AdoptionStatus {
        AVAILABLE,
        PENDING,
        ADOPTED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getLocationLink() {
        return locationLink;
    }

    public void setLocationLink(String locationLink) {
        this.locationLink = locationLink;
    }

    public String getVaccinationStatus() {
        return vaccinationStatus;
    }

    public void setVaccinationStatus(String vaccinationStatus) {
        this.vaccinationStatus = vaccinationStatus;
    }

    public Boolean getStray() {
        return stray;
    }

    public void setStray(Boolean stray) {
        this.stray = stray;
    }

    public AdoptionStatus getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(AdoptionStatus adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public User getListedBy() {
        return listedBy;
    }

    public void setListedBy(User listedBy) {
        this.listedBy = listedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
