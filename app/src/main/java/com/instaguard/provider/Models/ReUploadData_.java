package com.instaguard.provider.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by system7 on 11/6/2017.
 */

public class ReUploadData_ {

    @SerializedName("document_proof")
    @Expose
    private String documentProof;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("certificate")
    @Expose
    private String certificate;
    @SerializedName("driver_id")
    @Expose
    private String driverId;

    public String getDocumentProof() {
        return documentProof;
    }

    public void setDocumentProof(String documentProof) {
        this.documentProof = documentProof;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

}