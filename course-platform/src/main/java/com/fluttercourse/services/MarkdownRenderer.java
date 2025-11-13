package com.fluttercourse.services;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.Arrays;
import java.util.List;

/**
 * Renders Markdown content to HTML with syntax highlighting
 */
public class MarkdownRenderer {
    private Parser parser;
    private HtmlRenderer renderer;

    public MarkdownRenderer() {
        // Enable GitHub Flavored Markdown extensions
        List<Extension> extensions = Arrays.asList(
            TablesExtension.create(),
            HeadingAnchorExtension.create()
        );

        this.parser = Parser.builder()
            .extensions(extensions)
            .build();

        this.renderer = HtmlRenderer.builder()
            .extensions(extensions)
            .build();
    }

    /**
     * Convert markdown text to HTML
     */
    public String renderToHtml(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "<p>No content available</p>";
        }

        Node document = parser.parse(markdown);
        String html = renderer.render(document);

        // Wrap in a styled container
        return wrapInHtmlTemplate(html);
    }

    /**
     * Wrap the rendered HTML in a complete HTML document with styling
     */
    private String wrapInHtmlTemplate(String content) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 900px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f5f5f5;
                    }
                    h1 {
                        color: #2c3e50;
                        border-bottom: 3px solid #3498db;
                        padding-bottom: 10px;
                        margin-top: 0;
                    }
                    h2 {
                        color: #34495e;
                        margin-top: 30px;
                        border-left: 4px solid #3498db;
                        padding-left: 10px;
                    }
                    h3 {
                        color: #546e7a;
                        margin-top: 20px;
                    }
                    code {
                        background-color: #f4f4f4;
                        padding: 2px 6px;
                        border-radius: 3px;
                        font-family: 'Courier New', monospace;
                        font-size: 0.9em;
                        color: #c7254e;
                    }
                    pre {
                        background-color: #2d2d2d;
                        color: #f8f8f2;
                        padding: 15px;
                        border-radius: 5px;
                        overflow-x: auto;
                        line-height: 1.4;
                    }
                    pre code {
                        background-color: transparent;
                        color: #f8f8f2;
                        padding: 0;
                    }
                    blockquote {
                        border-left: 4px solid #3498db;
                        margin: 20px 0;
                        padding: 10px 20px;
                        background-color: #ecf0f1;
                        font-style: italic;
                    }
                    ul, ol {
                        margin: 15px 0;
                        padding-left: 30px;
                    }
                    li {
                        margin: 8px 0;
                    }
                    table {
                        border-collapse: collapse;
                        width: 100%;
                        margin: 20px 0;
                    }
                    th, td {
                        border: 1px solid #ddd;
                        padding: 12px;
                        text-align: left;
                    }
                    th {
                        background-color: #3498db;
                        color: white;
                    }
                    tr:nth-child(even) {
                        background-color: #f2f2f2;
                    }
                    .success {
                        background-color: #d4edda;
                        border-left: 4px solid #28a745;
                        padding: 15px;
                        margin: 15px 0;
                        border-radius: 4px;
                    }
                    .warning {
                        background-color: #fff3cd;
                        border-left: 4px solid #ffc107;
                        padding: 15px;
                        margin: 15px 0;
                        border-radius: 4px;
                    }
                    .info {
                        background-color: #d1ecf1;
                        border-left: 4px solid #17a2b8;
                        padding: 15px;
                        margin: 15px 0;
                        border-radius: 4px;
                    }
                    a {
                        color: #3498db;
                        text-decoration: none;
                    }
                    a:hover {
                        text-decoration: underline;
                    }
                    img {
                        max-width: 100%;
                        height: auto;
                        border-radius: 5px;
                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                    }
                </style>
            </head>
            <body>
                """ + content + """
            </body>
            </html>
            """;
    }
}
