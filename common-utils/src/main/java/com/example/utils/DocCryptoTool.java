package com.example.utils;

import org.hashids.Hashids;

public class DocCryptoTool implements CryptoTool{
    private final Hashids hashids;

    public DocCryptoTool(String salt) {
        var minHashLength = 15;
        this.hashids = new Hashids(salt, minHashLength);
    }

    @Override
    public String hashOf(Long value) {
        return hashids.encode(value);
    }

    @Override
    public Long idOf(String value) {
        long[] res = hashids.decode(value);
        if(res != null && res.length > 0) {
            return res[0];
        }
        return null;
    }
}
