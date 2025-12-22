package com.jacob.ventas.mappers;

public interface CommonMapper <RQ, S, E>{

    E lineCsvToVenta (S entity);

    E requestToEntity(RQ request);
}
