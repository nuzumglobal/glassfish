/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package test;

import java.io.IOException;
import java.lang.Exception;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import javax.servlet.Servlet;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.ProtocolHandler;
import javax.servlet.http.WebConnection;

import org.glassfish.grizzly.GrizzlyFuture;


import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.impl.FutureImpl;
import org.glassfish.grizzly.impl.SafeFutureImpl;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.grizzly.websockets.*;


public class TestProtocolHandler implements ProtocolHandler {

    protected static final int PORT = 1725;

    public void init(WebConnection wc) {

        try {
            run();
            /*TestTread test = new TestThread("test", wc.getOutputStream());
            test.start();  */
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private class TestTread extends Thread {
        String name;
        ServletOutputStream outputStream;

        TestTread(String name, ServletOutputStream out) {
            this.name = name;
            this.outputStream = out;
        }

        public void run() {
            try {
                while ( true ) {
                    System.out.println(name + System.currentTimeMillis());
                    if (outputStream != null) {
                        outputStream.print(name + System.currentTimeMillis());
                    }
                    sleep( 5000 );
                }
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    private void run() throws Exception {

        final CountDownLatch latch = new CountDownLatch(1);
        final EchoWebSocketApplication app = new EchoWebSocketApplication();
        WebSocketEngine.getEngine().register(app);
        HttpServer httpServer = HttpServer.createSimpleServer(".", PORT);
        httpServer.getServerConfiguration().setHttpServerName("WebSocket Server");
        httpServer.getServerConfiguration().setName("WebSocket Server");
        for (NetworkListener networkListener : httpServer.getListeners()) {
            networkListener.registerAddOn(new WebSocketAddOn());
        }
        httpServer.start();

        try {
            WebSocketClient socket = new WebSocketClient(String.format("ws://localhost:%s/echo", PORT),
                    new WebSocketAdapter() {
                        public void onMessage(WebSocket socket, String frame) {
                            latch.countDown();
                        }
                    });
            socket.connect();
            socket.send("echo me back");
            //Assert.assertTrue(latch.await(WebSocketEngine.DEFAULT_TIMEOUT, TimeUnit.SECONDS));
        } finally {
            //WebSocketEngine.getEngine().unregister(app);
            //httpServer.stop();
        }
    }

    /*
    public static class CountDownAdapter extends WebSocketAdapter {
        private final Set<String> sent;
        private final CountDownLatch received;
        private final CountDownLatch connected;

        public CountDownAdapter(Set<String> sent, CountDownLatch received, CountDownLatch connected) {
            this.sent = sent;
            this.received = received;
            this.connected = connected;
        }

        @Override
        public void onMessage(WebSocket socket, String data) {
            sent.remove(data);
            received.countDown();
        }

        @Override
        public void onConnect(WebSocket socket) {
            connected.countDown();
        }
    } */

}
