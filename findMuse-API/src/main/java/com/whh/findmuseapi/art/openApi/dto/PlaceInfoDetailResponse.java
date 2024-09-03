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
                detail.callNum,
                detail.latitude,
                detail.longitude,
                detail.park
        );
    }

    @NoArgsConstructor
    public static class Details {
        private String callNum;     //전화번호
        private String address;     //주소
        private String latitude;    //위도
        private String longitude;   //경도
        private String park;        //주차장 여부

        @XmlElement(name = "telno")
        public String getCallNum() {
            return callNum;
        }

        public void setCallNum(String callNum) {
            this.callNum = callNum;
        }

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

        @XmlElement(name = "parkinglot")
        public String getPark() {
            return park;
        }

        public void setPark(String park) {
            this.park = park;
        }
    }
}
