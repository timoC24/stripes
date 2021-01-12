package net.sourceforge.stripes.controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import net.sourceforge.stripes.FilterEnabledTestBase;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.mock.MockRoundtrip;


/**
 * Tests that make sure the basic functions of the ActionResolver work as expected.
 *
 * @author Tim Fennell
 */
@UrlBinding("/BasicResolverTests.action")
public class BasicResolverTests extends FilterEnabledTestBase implements ActionBean {

   private ActionBeanContext context;
   private int               number;

   @Override
   public ActionBeanContext getContext() { return context; }

   public int getNumber() { return number; }

   @DefaultHandler
   @HandlesEvent("one")
   public Resolution one() {
       number = 1;
      return null;
   }

   public Resolution process() {
      return null;
   }

   @Override
   public void setContext( ActionBeanContext context ) { this.context = context; }

   public void setNumber( int number ) { this.number = number; }

   @Test(groups = "fast")
   public void testDefaultResolution() throws Exception {
      MockRoundtrip trip = new MockRoundtrip(getMockServletContext(), getClass());
      trip.execute();

      BasicResolverTests bean = trip.getActionBean(getClass());
      Assert.assertEquals(bean.getNumber(), 1);
   }

   // Start of Test Methods

   @Test(groups = "fast")
   public void testEventNameParameterResolution() throws Exception {
      MockRoundtrip trip = new MockRoundtrip(getMockServletContext(), getClass());
      trip.addParameter(StripesConstants.URL_KEY_EVENT_NAME, "two");
      trip.execute();

      BasicResolverTests bean = trip.getActionBean(getClass());
      Assert.assertEquals(bean.getNumber(), 2);
      Assert.assertEquals(bean.getContext().getEventName(), "two");
   }

   @Test(groups = "fast")
   public void testImageStyleResolution() throws Exception {
      MockRoundtrip trip = new MockRoundtrip(getMockServletContext(), getClass());
      trip.execute("two.x");

      BasicResolverTests bean = trip.getActionBean(getClass());
      Assert.assertEquals(bean.getNumber(), 2);
   }

   @Test(groups = "fast")
   public void testImageStyleResolution2() throws Exception {
      MockRoundtrip trip = new MockRoundtrip(getMockServletContext(), getClass());
      trip.addParameter("two.x", "381");
      trip.execute();

      BasicResolverTests bean = trip.getActionBean(getClass());
      Assert.assertEquals(bean.getNumber(), 2);
   }

   @Test(groups = "fast")
   public void testNonDefaultResolution() throws Exception {
      MockRoundtrip trip = new MockRoundtrip(getMockServletContext(), getClass());
      trip.execute("two");

      BasicResolverTests bean = trip.getActionBean(getClass());
      Assert.assertEquals(bean.getNumber(), 2);
   }

   @Test(groups = "fast")
   public void testOverrideHandlerMethodReturnsSubtype() throws SecurityException, NoSuchMethodException {
      NameBasedActionResolver resolver = new NameBasedActionResolver();
      Map<String, Method> classMappings = new HashMap<>();
      resolver.processMethods(ExtendedBaseAction.class, classMappings);
   }

   @HandlesEvent("two")
   public Resolution two() {
       number = 2;
      return null;
   }


   public static class ExtendedBaseAction extends BasicResolverTests {

      @Override
      public ForwardResolution process() {
         return null;
      }
   }
}
