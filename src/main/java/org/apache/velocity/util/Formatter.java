package org.apache.velocity.util;

import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.util.introspection.Info;

/**
 * <p>Formatter is ...
 * </p>
 * User: <a href="email:fbrier@multideck.com">Frederick N. Brier</a><br/>
 * Created: 9/17/14 6:37 AM<br/>
 * <p/>
 * Copyright @2014 Multideck Corporation.  All rights reserved.<br/>
 */
public class Formatter
{
    /**
     * Creates a string that formats the template filename with line number
     * and column of the given Directive. We use this routine to provide a cosistent format for displaying
     * file errors.
     */
    public static final String formatFileString(Directive directive)
    {
        return formatFileString(directive.getTemplateName(), directive.getLine(), directive.getColumn());
    }

    /**
     * Creates a string that formats the template filename with line number
     * and column of the given Node. We use this routine to provide a cosistent format for displaying
     * file errors.
     */
    public static final String formatFileString(Node node)
    {
        return formatFileString(node.getTemplateName(), node.getLine(), node.getColumn());
    }

    /**
     * Simply creates a string that formats the template filename with line number
     * and column. We use this routine to provide a cosistent format for displaying
     * file errors.
     */
    public static final String formatFileString(Info info)
    {
        return formatFileString(info.getTemplateName(), info.getLine(), info.getColumn());
    }

    /**
     * Simply creates a string that formats the template filename with line number
     * and column. We use this routine to provide a cosistent format for displaying
     * file errors.
     * @param template File name of template, can be null
     * @param linenum Line number within the file
     * @param colnum Column number withing the file at linenum
     */
    public static final String formatFileString(String template, int linenum, int colnum)
    {
        if (template == null || template.equals(""))
        {
            template = "<unknown template>";
        }
        return template + "[line " + linenum + ", column " + colnum + "]";
    }

}
