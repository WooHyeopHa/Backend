package com.whh.findmuseapi.art.openApi.dto;


import com.whh.findmuseapi.art.entity.Art;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@NoArgsConstructor
@XmlRootElement(name = "dbs")
public class PlaceInfoDetailResponse {

    private Details detail;

    @XmlElement(name = "db")
    public Details getDetail() {
        return detail;
    }

    public void setDetail(Details detail) {
        this.detail = detail;
    }

    public void toEntity(Art art) {
         art.setPlaceDetailInfo(
                detail.getAddress(),
                detail.latitude,
                detail.longitude,
                detail.sPark,
                detail.park
        );
    }

    @NoArgsConstructor
    public static class Details {
        private String address;     //주소
        private String latitude;    //위도
        private String longitude;   //경도
        private String sPark;       //장애인 주차장 여부
        private String park;        //주차장 여부


        @XmlElement(name = "adres")
        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @XmlElement(name = "la")
        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        @XmlElement(name = "lo")
        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        @XmlElement(name = "parkbarrier")
        public String getsPark() {
            return sPark;
        }

        public void setsPark(String sPark) {
            this.sPark = sPark;
        }

        @XmlElement(name = "parkinglot")
        public String getPark() {
            return park;
        }

        public void setPark(String park) {
            this.park = park;
        }
    }
}
