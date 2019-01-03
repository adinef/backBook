package net.fp.backBook.security.service;

public interface TokenService {

    String getToken(String username, String password);
}
