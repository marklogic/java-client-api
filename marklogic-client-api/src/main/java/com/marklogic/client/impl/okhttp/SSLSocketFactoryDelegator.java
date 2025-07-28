/*
 * Copyright © 2024 MarkLogic Corporation. All Rights Reserved.
 */
package com.marklogic.client.impl.okhttp;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

class SSLSocketFactoryDelegator extends SSLSocketFactory {
    private final SSLSocketFactory delegate;

    SSLSocketFactoryDelegator(SSLSocketFactory delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }
    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return configureSocket(delegate.createSocket(s, host, port, autoClose));
    }
    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return configureSocket(delegate.createSocket(host, port));
    }
    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException {
        return configureSocket(delegate.createSocket(host, port, localAddress, localPort));
    }
    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return configureSocket(delegate.createSocket(address, port));
    }
    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return configureSocket(delegate.createSocket(address, port, localAddress, localPort));
    }
    @Override
    public Socket createSocket(Socket s, InputStream consumed, boolean autoClose) throws IOException {
        return configureSocket(delegate.createSocket(s, consumed, autoClose));
    }
    @Override
    public Socket createSocket() throws IOException {
        return configureSocket(delegate.createSocket());
    }

    private Socket configureSocket(Socket socket) throws SocketException {
        socket.setKeepAlive(true);
        return socket;
    }
}
