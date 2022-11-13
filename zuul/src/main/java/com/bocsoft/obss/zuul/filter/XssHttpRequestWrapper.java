package com.bocsoft.obss.zuul.filter;

import com.netflix.zuul.http.HttpServletRequestWrapper;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * 防止Xss攻击
 */
public class XssHttpRequestWrapper extends HttpServletRequestWrapper {

    @Override
    public String getParameter(String name) {
        return StringEscapeUtils.escapeHtml(super.getParameter(name));
    }

    @Override
    public String getHeader(String name) {
        return StringEscapeUtils.escapeHtml(super.getHeader(name));
    }

    @Override
    public String getQueryString() {
        return StringEscapeUtils.escapeHtml(super.getQueryString());
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            String [] escapeValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                escapeValues[i] = StringEscapeUtils.escapeHtml(values[i]);
            }
            return escapeValues;
        }
        return super.getParameterValues(name);
    }
}
