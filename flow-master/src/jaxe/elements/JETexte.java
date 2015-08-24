/*
Jaxe - Editeur XML en Java

Copyright (C) 2002 Observatoire de Paris-Meudon

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
*/

package jaxe.elements;

import org.apache.log4j.Logger;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

import jaxe.JaxeDocument;
import jaxe.JaxeElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Zone de texte interne � Jaxe (il n'y a pas de type correspondant). Ne doit jamais avoir d'enfants.
 */
public class JETexte extends JaxeElement {
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(JETexte.class);

    public JETexte(final JaxeDocument doc) {
        this.doc = doc;
    }
    
    @Override
    public void init(final Position pos, final Node noeud) {
        final String texte = noeud.getNodeValue();
        insertText(pos, texte);
    }
    
    @Override
    public Node nouvelElement(final Element refElement) {
        return null;
    }
    
    public static JETexte nouveau(final JaxeDocument doc, final Position debut, final Position fin, final String texte) {
        final Node textnode = doc.DOMdoc.createTextNode(texte);
        final JETexte newje = new JETexte(doc);
        newje.debut = debut;
        newje.fin = fin;
        newje.noeud = textnode;
        doc.dom2JaxeElement.put(textnode, newje);
        return newje;
    }
    
    /**
     * Coupe la zone de texte en 2, retourne le nouveau JETexte cr�� apr�s celui-ci.
     */
    @Override
    public JaxeElement couper(final Position pos) {
        final String t = noeud.getNodeValue();
        final String t1 = t.substring(0, pos.getOffset() - debut.getOffset());
        final String t2 = t.substring(pos.getOffset() - debut.getOffset());
        noeud.setNodeValue(t1);
        final Node textnode2 = doc.DOMdoc.createTextNode(t2);
        final Node nextnode = noeud.getNextSibling();
        final JaxeElement parent = getParent();
        if (nextnode == null)
            parent.noeud.appendChild(textnode2);
        else
            parent.noeud.insertBefore(textnode2, nextnode);
        final JETexte je2 = new JETexte(doc);
        je2.noeud = textnode2;
        je2.doc = parent.doc;
        try {
            je2.debut = doc.createPosition(pos.getOffset());
            je2.fin = fin;
            fin = doc.createPosition(pos.getOffset() - 1);
        } catch (final BadLocationException ex) {
            LOG.error("couper() - BadLocationException", ex);
        }
        doc.dom2JaxeElement.put(je2.noeud, je2);
        return (je2);
    }

}
