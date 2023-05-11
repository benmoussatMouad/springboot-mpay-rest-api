package com.springboot.mpaybackend.service;

import com.springboot.mpaybackend.entity.Wilaya;
import com.springboot.mpaybackend.payload.WilayaDto;

import java.util.List;

public interface WilayaService {

    WilayaDto addWilaya(WilayaDto dto);

    WilayaDto getWilaya(Long wilayaId);

    List<WilayaDto> getAllWilayas();

    WilayaDto updateWilaya(WilayaDto dto, Long wilayaId);

    void deleteWilaya(Long wilayaId);

}
