package ixa.kaflib;

import org.jdom2.Content;
import org.jdom2.IllegalDataException;
import org.jdom2.Parent;
import org.jdom2.Verifier;

/**
 * An XML comment. Methods allow the user to get and set the text of the
 * comment.
 *
 * @author  Brett McLaughlin
 * @author  Jason Hunter
 */
public class Comment extends org.jdom2.Comment {

	public Comment(String text) {
		super();

		text = text.trim();
		text = text.replaceAll("-+", "-");
		if (text.endsWith("-")) {
			text += " .";
		}

		setText(text);
	}

}