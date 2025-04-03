import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class SgfParsing {
    public SgfNode parse(String input) throws SgfParsingException {
        if (input == null || input.isEmpty()) {
            throw new SgfParsingException("Input is empty");
        }
        input = input.trim();
        if (!input.startsWith("(") || !input.endsWith(")")) {
            throw new SgfParsingException("Tree missing");
        }
        Parser parser = new Parser(input);
        SgfNode root = parser.parseTree();
        if (!parser.isAtEnd()) {
            throw new SgfParsingException("Unexpected trailing data");
        }
        return root;
    }

    private static class Parser {
        private final String input;
        private int pos;

        Parser(String input) {
            this.input = input;
            this.pos = 0;
        }

        boolean isAtEnd() {
            return pos >= input.length();
        }

        char peek() {
            return input.charAt(pos);
        }

        char next() {
            return input.charAt(pos++);
        }

        void expect(char c) throws SgfParsingException {
            if (isAtEnd() || input.charAt(pos) != c) {
                throw new SgfParsingException("Expected '" + c + "'");
            }
            pos++;
        }

        SgfNode parseTree() throws SgfParsingException {
            skipWhitespace();
            expect('(');
            skipWhitespace();
            if (isAtEnd()) {
                throw new SgfParsingException("Unexpected end of input");
            }
            if (peek() != ';') {
                throw new SgfParsingException("Tree with no nodes");
            }
            SgfNode root = parseNode();
            SgfNode current = root;
            skipWhitespace();
            // Handle sequence of sibling nodes
            while (!isAtEnd() && peek() == ';') {
                SgfNode nextNode = parseNode();
                current.appendChild(nextNode);
                current = nextNode;
                skipWhitespace();
            }
            // Handle child trees (branches)
            while (!isAtEnd() && peek() == '(') {
                SgfNode childTree = parseTree();
                current.appendChild(childTree);
                skipWhitespace();
            }
            expect(')');
            return root;
        }

        SgfNode parseNode() throws SgfParsingException {
            expect(';');
            Map<String, List<String>> props = new HashMap<>();
            skipWhitespace();
            while (!isAtEnd()) {
                char c = peek();
                if (c == ';' || c == '(' || c == ')') {
                    break;
                }
                String key = parsePropIdent();
                if (!key.matches("[A-Z]+")) {
                    throw new SgfParsingException("Property must be uppercase");
                }
                List<String> values = new ArrayList<>();
                skipWhitespace();
                if (isAtEnd() || peek() != '[') {
                    throw new SgfParsingException("Property without delimiter");
                }
                while (!isAtEnd() && peek() == '[') {
                    values.add(parsePropValue());
                    skipWhitespace();
                }
                props.put(key, values);
            }
            return new SgfNode(props);
        }

        String parsePropIdent() throws SgfParsingException {
            StringBuilder sb = new StringBuilder();
            while (!isAtEnd()) {
                char c = peek();
                if (c >= 'A' && c <= 'Z') {
                    sb.append(c);
                    pos++;
                } else if (c >= 'a' && c <= 'z') {
                    sb.append(c);
                    pos++;
                } else {
                    break;
                }
            }
            if (sb.length() == 0) {
                throw new SgfParsingException("Expected property identifier");
            }
            return sb.toString();
        }

        String parsePropValue() throws SgfParsingException {
            expect('[');
            StringBuilder sb = new StringBuilder();
            while (!isAtEnd()) {
                char c = next();
                if (c == ']') {
                    return unescapePropValue(sb.toString());
                } else if (c == '\\') {
                    if (isAtEnd()) {
                        break;
                    }
                    char nextChar = next();
                    sb.append('\\').append(nextChar);
                } else {
                    sb.append(c);
                }
            }
            throw new SgfParsingException("Unclosed property value");
        }

        String unescapePropValue(String raw) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < raw.length(); i++) {
                char c = raw.charAt(i);
                if (c == '\\' && i + 1 < raw.length()) {
                    char nextChar = raw.charAt(i + 1);
                    if (nextChar == ']' || nextChar == '\\') {
                        sb.append(nextChar);
                        i++;
                    } else {
                        sb.append(nextChar);
                        i++;
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        void skipWhitespace() {
            while (!isAtEnd()) {
                char c = peek();
                if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                    pos++;
                } else {
                    break;
                }
            }
        }
    }
}
