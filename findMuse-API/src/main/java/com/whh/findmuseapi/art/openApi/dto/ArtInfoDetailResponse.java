package com.whh.findmuseapi.art.openApi.dto;

import com.whh.findmuseapi.art.entity.Art;
import com.whh.findmuseapi.common.constant.Infos;
import com.whh.findmuseapi.file.entity.File;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "dbs")
@NoArgsConstructor
public class ArtInfoDetailResponse {

    private Details detail;

    @XmlElement(name = "db")
    public Details getDetail() {
        return detail;
    }

    public void setDetail(Details detail) {
        this.detail = detail;
    }

    public Art toEntity(Infos.ArtType type) {

        Art newArt = Art.builder()
                .title(detail.title)
                .artType(type)
                .place(detail.place)
                .startDate(detail.startDate)
                .endDate(detail.endDate).build();

        List<File> photos = detail.getPhotos().stream()
                .map(s -> new File(s, newArt)).toList();


        List<com.whh.findmuseapi.art.entity.Ticket> tickets = detail.getTickets().stream()
                .map(t ->
                    com.whh.findmuseapi.art.entity.Ticket.builder()
                            .name(t.platform)
                            .url(t.platformUrl)
                            .art(newArt).build()).toList();

        newArt.setFilesAndTickets(photos, tickets);
        return newArt;
    }

    @NoArgsConstructor
    public static class Details {

        private String title;
        private String startDate;
        private String endDate;
        private List<String> photos = new ArrayList<>();
        private String place;
        private String placeId;
        private String startTime;
        private List<Ticket> tickets;


        @XmlElement(name = "prfnm")
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @XmlElement(name = "prfpdfrom")
        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        @XmlElement(name = "prfpdto")
        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }


        @XmlElementWrapper(name = "styurls")
        @XmlElement(name = "styurl")
        public List<String> getPhotos() {
            return photos;
        }

        public void setPhotos(List<String> photos) {
            this.photos = photos;
        }

        @XmlElement(name = "fcltynm")
        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        @XmlElement(name = "mt10id")
        public String getPlaceId() {
            return placeId;
        }

        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        @XmlElement(name = "dtguidance")
        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        @XmlElementWrapper(name = "relates")
        @XmlElement(name = "relate")
        public List<Ticket> getTickets() {
            return tickets;
        }

        public void setTickets(List<Ticket> tickets) {
            this.tickets = tickets;
        }
    }

    /**
     * 예매 티켓 정보
     */
    @NoArgsConstructor
    @XmlRootElement(name = "relate")
    public static class Ticket {

        private String platform;
        private String platformUrl;

        @XmlElement(name = "relatenm")
        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        @XmlElement(name = "relateurl")
        public String getPlatformUrl() {
            return platformUrl;
        }

        public void setPlatformUrl(String platformUrl) {
            this.platformUrl = platformUrl;
        }
    }
}
