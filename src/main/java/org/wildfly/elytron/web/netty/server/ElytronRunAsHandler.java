/*
 * Copyright 2019 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wildfly.elytron.web.netty.server;

import java.util.function.Supplier;

import org.wildfly.common.function.ExceptionFunction;
import org.wildfly.security.auth.server.SecurityIdentity;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * The {@link ChannelInboundHandler} responsible for associating the current {@link SecurityIdentity}.
 *
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 */
class ElytronRunAsHandler extends ChannelInboundHandlerAdapter {

    private final Supplier<SecurityIdentity> securityIdentitySupplier;

    ElytronRunAsHandler(final Supplier<SecurityIdentity> securityIdentitySupplier) {
        this.securityIdentitySupplier = securityIdentitySupplier;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("ElytronRunAsHandler.read()");
        SecurityIdentity securityIdentity = securityIdentitySupplier.get();
        if (securityIdentity != null) {
            System.out.println("ElytronRunAsHandler - " + securityIdentity.getPrincipal().getName());
            securityIdentity.runAsFunctionEx((ExceptionFunction<Void, Void, Exception>) (v) -> {
                super.channelRead(ctx, msg);
                return null;
            }, null);
        } else {
            System.out.println("ElytronRunAsHandler - No Identity");
            super.channelRead(ctx, msg);
        }
    }

}
