package br.com.bravatec.webdesk.util;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class XmlBinder extends DefaultHandler{
	private StringBuffer tagContent = new StringBuffer();
	private ArrayListStack<Object> current;
	private ArrayListStack<Type> listTypes;

	public <T>T bind(Class<T> klass, InputStream xml) {
		T ret = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		System.out.println(ret);
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		current = new ArrayListStack<Object>();
		listTypes = new ArrayListStack<Type>();

		try {
			ret = klass.newInstance();
			current.push(ret);

			SAXParser parser = factory.newSAXParser();
			parser.parse(xml, this );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		tagContent.delete(0, tagContent.length());
		//ignora tags com name space (soap e ws)
		if(qName.indexOf(':') == -1) {
			Method getter = null;
			try {
				getter = current.peek().getClass().getMethod("get" + qName.substring(0,1).toUpperCase()  + qName.substring(1),new Class[0]);
			}catch (NoSuchMethodException e) {
				if(current.peek() instanceof List) {
					try {
						Object o = ((Class)listTypes.peek()).newInstance();
						((List)current.peek()).add(o);
						current.push(o);
					}catch (Exception e1) {
						e1.printStackTrace();
					}

				} else {
					current.push(qName);
				}
				return;
			}
			try {
				if(List.class.isAssignableFrom(getter.getReturnType())) {
					Method setter = current.peek().getClass().getMethod("set" + qName.substring(0,1).toUpperCase()  + qName.substring(1),new Class[]{getter.getReturnType()});
					ArrayList<Object> l = new ArrayList<Object>();

					setter.invoke(current.peek(), l);
					ParameterizedType generic = (ParameterizedType)getter.getGenericReturnType();

					listTypes.push(generic.getActualTypeArguments()[0]);
					current.push(l);
				} else if(getter.getReturnType().isPrimitive()
						|| Number.class.isAssignableFrom(getter.getReturnType()) 
						|| String.class.isAssignableFrom(getter.getReturnType())
						|| Date.class.isAssignableFrom(getter.getReturnType())
						|| Calendar.class.isAssignableFrom(getter.getReturnType())
						|| Boolean.class.isAssignableFrom(getter.getReturnType())){
					current.push(qName);
				} else {
					Method setter = current.peek().getClass().getMethod("set" + qName.substring(0,1).toUpperCase()  + qName.substring(1),new Class[]{getter.getReturnType()});
					Object o = getter.getReturnType().newInstance();
					setter.invoke(current.peek(), o);
					current.push(o);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		tagContent.append(ch,start,length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.indexOf(':') == -1) {
			current.pop();
			Method getter = null;
			try{
				getter = current.peek().getClass().getMethod("get" + qName.substring(0,1).toUpperCase()  + qName.substring(1),new Class[0]);
			}catch (NoSuchMethodException e) {
				return;
			}
			if(List.class.isAssignableFrom(getter.getReturnType())) {
				listTypes.pop();
			} else if(getter.getReturnType().isPrimitive()
					|| Number.class.isAssignableFrom(getter.getReturnType()) 
					|| String.class.isAssignableFrom(getter.getReturnType())
					|| Date.class.isAssignableFrom(getter.getReturnType())
					|| Calendar.class.isAssignableFrom(getter.getReturnType())
					|| Boolean.class.isAssignableFrom(getter.getReturnType())){
				setValue(tagContent.toString(), qName, getter.getReturnType());
			} else {
			}
		}

	}

	private void setValue(Object value, String property, Class<?> type) {
		Object newVal = null;
		//TODO:Mapear todos os valores
		if(String.class.equals(type)) {
			newVal = value;
		} else if(Integer.class.equals(type) || Integer.TYPE.equals(type)){
			newVal = Integer.parseInt((String)value);
		} else if(Long.class.equals(type)  || Long.TYPE.equals(type)){
			newVal = Long.parseLong((String)value);
		} else if(Double.class.equals(type)  || Double.TYPE.equals(type)){
			newVal = Double.parseDouble((String)value);
		} else if(Float.class.equals(type) || Float.TYPE.equals(type)){
			newVal = Float.parseFloat((String)value);
		} else if(Byte.class.equals(type) || Byte.TYPE.equals(type)){
			newVal = Byte.parseByte((String)value);
		} else if(BigDecimal.class.equals(type)){
			newVal = new BigDecimal((String)value);
		} else if(Boolean.class.equals(type) || Boolean.TYPE.equals(type)){
			newVal = "true".equals(value);
		} else if(Date.class.equals(type)){
			try {
				newVal = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse((String)value);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if(Calendar.class.equals(type)){
			try {Calendar c = Calendar.getInstance();

			c.setTime(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").parse((String)value));
			newVal = c;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		try {
			Method setter = current.peek().getClass().getMethod("set" + property.substring(0,1).toUpperCase()  + property.substring(1),new Class[]{type});
			setter.invoke(current.peek(), new Object[]{newVal});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
