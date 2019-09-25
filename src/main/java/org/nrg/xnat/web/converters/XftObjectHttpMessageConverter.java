package org.nrg.xnat.web.converters;

import org.nrg.xdat.XDAT;
import org.nrg.xdat.base.BaseElement;
import org.nrg.xft.ItemI;
import org.nrg.xft.schema.Wrappers.XMLWrapper.SAXReader;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Handles converting XFT classes
 */
@Component
public class XftObjectHttpMessageConverter extends AbstractHttpMessageConverter<ItemI> {
    public XftObjectHttpMessageConverter() {
        super(MediaType.APPLICATION_XML);
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return ItemI.class.isAssignableFrom(clazz);
    }

    @Override
    protected ItemI readInternal(final Class<? extends ItemI> clazz, final HttpInputMessage message) throws IOException, HttpMessageNotReadableException {
        try (final InputStreamReader input = new InputStreamReader(message.getBody())) {
            return BaseElement.GetGeneratedItem(new SAXReader(XDAT.getUserDetails()).parse(new InputSource(input)));
        } catch (SAXException e) {
            throw new HttpMessageNotReadableException("An error occurred trying to parse the submitted XML", e);
        }
    }

    @Override
    protected void writeInternal(final ItemI item, final HttpOutputMessage message) throws HttpMessageNotWritableException {
        try (final Writer output = new OutputStreamWriter(message.getBody())) {
            item.toXML(output, true);
        } catch (Exception e) {
            throw new HttpMessageNotReadableException("An error occurred writing the provided object", e);
        }
    }
}
