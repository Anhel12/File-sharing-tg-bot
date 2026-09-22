package com.example.utils;

import org.hashids.Hashids;

public interface CryptoTool {
    String hashOf(Long value);
    Long idOf(String value);
}
