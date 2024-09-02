package com.whh.findmuseapi.art.openApi.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
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

    @NoArgsConstructor
    public static class Details {

        private String artId;
        private String title;
        private String startDate;
        private String endDate;
        private String runTime;
        private String price;
        private String state;
        private List<String> photos;
        private String placeId;
        private String startTime;
        private List<Ticket> tickets;

        @XmlElement(name = "mt20id")
        public String getArtId() {
            return artId;
        }

        public void setArtId(String artId) {
            this.artId = artId;
        }

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

        @XmlElement(name = "prfruntime")
        public String getRunTime() {
            return runTime;
        }

        public void setRunTime(String runTime) {
            this.runTime = runTime;
        }

        @XmlElement(name = "pcseguidance")
        public String getPrice() {
            return price;
        }


        public void setPrice(String price) {
            this.price = price;
        }

        @XmlElement(name = "prfstate")
        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        @XmlElementWrapper(name = "styurls")
        @XmlElement(name = "styurl")
        public List<String> getPhotos() {
            return photos;
        }

        public void setPhotos(List<String> photos) {
            this.photos = photos;
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
