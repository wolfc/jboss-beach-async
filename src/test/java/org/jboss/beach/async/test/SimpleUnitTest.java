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
package org.jboss.beach.async.test;

import org.jboss.beach.async.Async;
import org.junit.Test;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jboss.beach.async.Async.async;
import static org.jboss.beach.async.Async.divine;
import static org.junit.Assert.assertEquals;

/**
 * @author <a href="cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class SimpleUnitTest
{
   public static interface SomeView
   {
      int getNumber() throws InterruptedException;

      void join(CyclicBarrier barrier) throws BrokenBarrierException, TimeoutException, InterruptedException;

      void doSomething() throws InterruptedException;
   }

   public static class SomeBean implements SomeView
   {
      public int getNumber() throws InterruptedException
      {
         Thread.sleep(2000);
         return 1;
      }

      public void join(CyclicBarrier barrier) throws BrokenBarrierException, TimeoutException, InterruptedException
      {
         barrier.await(5, SECONDS);
      }

      public void doSomething() throws InterruptedException
      {
         Thread.sleep(2000);
      }
   }

   @Test
   public void test1() throws Exception
   {
      SomeView bean = new SomeBean();
      Future<Integer> future = divine(async(bean).getNumber());
      int result = future.get(5, SECONDS);
      assertEquals(1, result);
   }

   @Test
   public void test2() throws Exception
   {
      CyclicBarrier barrier = new CyclicBarrier(3);

      SomeView bean = new SomeBean();

      async(bean).join(barrier);
      async(bean).join(barrier);

      barrier.await(5, SECONDS);
   }
}
