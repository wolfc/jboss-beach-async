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
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
class AsyncInvocationHandler implements InvocationHandler
{
   private ExecutorService executor;
   private InvocationHandler delegate;

   AsyncInvocationHandler(ExecutorService executor, InvocationHandler delegate)
   {
      this.executor = executor;
      this.delegate = delegate;
   }

   @Override
   public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable
   {
      ExecutorService executor = getExecutor();
      Callable<?> task = new Callable<Object>() {
         @Override
         public Object call() throws Exception
         {
            try
            {
               return delegate.invoke(proxy, method, args);
            }
            catch(Throwable t)
            {
               if(t instanceof Error)
                  throw (Error) t;
               if(t instanceof RuntimeException)
                  throw (RuntimeException) t;
               if(t instanceof Exception)
                  throw (Exception) t;
               // should never happen
               throw new RuntimeException(t);
            }
         }
      };
      Future<?> result = executor.submit(task);
      Async.currentResult.set(result);
      // TODO: analyze return type
      if(method.getReturnType().isPrimitive())
         return 0;
      return null;
   }

   protected ExecutorService getExecutor()
   {
      return executor;
   }
}
