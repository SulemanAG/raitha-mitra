package com.raithamitra.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.Instant;
import java.util.Objects;

/**
 * JPA Embeddable class representing geographic coordinates and administrative region metadata.
 *
 * @author Suleman Agasimani
 * @since 1.0
 */
@Embeddable
public class LocationData {

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "district", length = 100)
    private String district;

    @Column(name = "taluk", length = 100)
    private String taluk;

    @Column(name = "village", length = 100)
    private String village;

    @Enumerated(EnumType.STRING)
    @Column(name = "location_source", length = 30)
    private LocationSource locationSource = LocationSource.MANUAL;

    @Column(name = "location_updated_at")
    private Instant locationUpdatedAt;

    public LocationData() {
    }

    public LocationData(Double latitude, Double longitude, String state, String district, String taluk, String village, LocationSource locationSource) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
        this.district = district;
        this.taluk = taluk;
        this.village = village;
        this.locationSource = locationSource != null ? locationSource : LocationSource.MANUAL;
        this.locationUpdatedAt = Instant.now();
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTaluk() {
        return taluk;
    }

    public void setTaluk(String taluk) {
        this.taluk = taluk;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public LocationSource getLocationSource() {
        return locationSource;
    }

    public void setLocationSource(LocationSource locationSource) {
        this.locationSource = locationSource;
    }

    public Instant getLocationUpdatedAt() {
        return locationUpdatedAt;
    }

    public void setLocationUpdatedAt(Instant locationUpdatedAt) {
        this.locationUpdatedAt = locationUpdatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationData that = (LocationData) o;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude) &&
                Objects.equals(district, that.district) &&
                Objects.equals(village, that.village);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, district, village);
    }
}
