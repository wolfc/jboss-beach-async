/*
 * JBoss, Home of Professional Open Source
 * Copyright (c) 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.beach.async;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Async
{
   // TODO: obtain via other means
   private static ExecutorService executor = Executors.newFixedThreadPool(10);
   protected static ThreadLocal<Future<?>> currentResult = new ThreadLocal<Future<?>>();
   
   public static <T> T async(T bean)
   {
      /*
      if(Proxy.isProxyClass(bean.getClass()))
      {
         InvocationHandler handler = Proxy.getInvocationHandler(bean);
         if(handler instanceof AsyncInvocationHandler)
         {
            return bean;
         }
      }
      */
      ClassLoader loader = bean.getClass().getClassLoader();
      Class<?> interfaces[] = bean.getClass().getInterfaces();
      InvocationHandler handler = new AsyncInvocationHandler(executor, new DirectInvocationHandler(bean));
      return (T) Proxy.newProxyInstance(loader, interfaces, handler);
   }

   public static Future<?> divine()
   {
      return currentResult.get();
   }
   
   public static <R> Future<R> divine(R dummyResult)
   {
      return (Future<R>) currentResult.get();
   }
}
