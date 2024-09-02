package com.whh.findmuseapi.art.openApi.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "dbs")
public class ArtInfoResponse {

    private List<Db> dbList;

    public void setDbList(List<Db> dbList) {
        this.dbList = dbList;
    }

    @XmlElement(name = "db")
    public List<Db> getDbList() {
        return dbList;
    }

    @NoArgsConstructor
    @XmlRootElement(name = "db")
    public static class Db {

//        @JsonProperty("mt20id")
        private String id;

        @XmlElement(name = "mt20id")
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
