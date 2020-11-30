/*
 * Copyright (c) 2020 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.client.impl;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

class SocketFactoryDelegator extends SocketFactory {
    private final SocketFactory delegate;

    SocketFactoryDelegator(SocketFactory delegate) {
        super();
        this.delegate = delegate;
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
    public Socket createSocket() throws IOException {
        return configureSocket(delegate.createSocket());
    }

    private Socket configureSocket(Socket socket) throws SocketException {
        socket.setKeepAlive(true);
        return socket;
    }
}
