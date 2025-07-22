package com.mycompany.uas_project;

import java.time.LocalDate;

public class Kehadiran {
    private int id;
    private String nim;
    private LocalDate tanggal;
    private String status;

    public Kehadiran(int id, String nim, LocalDate tanggal, String status) {
        this.id = id;
        this.nim = nim;
        this.tanggal = tanggal;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getNim() {
        return nim;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
