/*
 Jaxe - Editeur XML en Java

 Copyright (C) 2002 Observatoire de Paris-Meudon

 Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU, telle que publi�e par la Free Software Foundation ; version 2 de la licence, ou encore (� votre choix) toute version ult�rieure.

 Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou D'ADAPTATION A UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence Publique G�n�rale GNU .

 Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, Etats-Unis.
 */

package jaxe;

import org.apache.log4j.Logger;

import java.awt.Container;
import java.awt.Toolkit;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.OverlayLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.EditorKit;
import javax.swing.text.IconView;
import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.undo.UndoableEdit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jaxe.elements.JECData;
import jaxe.elements.JECommentaire;
import jaxe.elements.JEDivision;
import jaxe.elements.JEInconnu;
import jaxe.elements.JEStyle;
import jaxe.elements.JESwing;
import jaxe.elements.JETableTexte;
import jaxe.elements.JETexte;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Classe repr�sentant un document XML
 */
public class JaxeDocument extends DefaultStyledDocument {
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(JaxeDocument.class);

    private static final ResourceBundle rb = JaxeResourceBundle.getRB();
    public org.w3c.dom.Document DOMdoc = null;
    public HashMap<Node, JaxeElement> dom2JaxeElement = null;
    public JaxeElement rootJE = null;
    public JaxeTextPane textPane;
    public File fsave = null; // r�f�rence sur le disque vers le fichier XML
    //  (null par exemple si le fichier n'est pas sauvegard� ou si il est acc�d� par le web)
    public URL furl = null; // URL vers le fichier XML
    public String encodage = "ISO-8859-1"; // valeur par d�faut
    public boolean modif = false; // utiliser getModif() et setModif() de pr�f�rence
    public Config cfg = null;
    public JFrame jframe;
    public String nomFichierCfg;

    final static String kPoliceParDefaut = "Serif";

    final static int kTailleParDefaut = 14;

    private InterfaceGestionErreurs gestionErreurs = new GestionErreurs(this);
    
    private final List<JaxeEditListenerIf> _editListener;
    
    private boolean _ignorer = false;
    
    
    public JaxeDocument() {
        super();
        setDefaultStyle();
        _editListener = new ArrayList<JaxeEditListenerIf>();
    }

    public JaxeDocument(final String nomFichierCfg) {
        super();
        this.nomFichierCfg = nomFichierCfg;
        if (nomFichierCfg != null) {
            try {
                cfg = new Config(nomFichierCfg, true);
            } catch (JaxeException ex) {
                JOptionPane.showMessageDialog(jframe, ex.getMessage(),
                    rb.getString("erreur.Erreur"), JOptionPane.ERROR_MESSAGE);
                cfg = null;
            }
        }
        setDefaultStyle();
        _editListener = new ArrayList<JaxeEditListenerIf>();
    }

    public JaxeDocument(final Config newconfig) {
        super();
        cfg = newconfig;
        setDefaultStyle();
        _editListener = new ArrayList<JaxeEditListenerIf>();
    }

    public JaxeDocument(final JaxeTextPane textPane, final String nomFichierCfg) {
        super();
        this.textPane = textPane;
        this.nomFichierCfg = nomFichierCfg;
        jframe = textPane.jframe;
        if (nomFichierCfg != null) {
            try {
                cfg = new Config(nomFichierCfg, true);
            } catch (JaxeException ex) {
                JOptionPane.showMessageDialog(jframe, ex.getMessage(),
                    rb.getString("erreur.Erreur"), JOptionPane.ERROR_MESSAGE);
                cfg = null;
            }
        }
        setDefaultStyle();
        _editListener = new ArrayList<JaxeEditListenerIf>();
    }

    /**
     * D�finie le gestionnaire d'erreurs pour le document
     */
    public void setGestionErreurs(final InterfaceGestionErreurs gestionErreurs) {
        this.gestionErreurs = gestionErreurs;
    }

    /**
     * Renvoit le gestionnaire d'erreurs du document
     */
    public InterfaceGestionErreurs getGestionErreurs() {
        return gestionErreurs;
    }

    private void setDefaultStyle() {
        final Style defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontFamily(defaultStyle, kPoliceParDefaut);
        StyleConstants.setFontSize(defaultStyle, kTailleParDefaut);
    }

    public void setTextPane(final JaxeTextPane textPane) {
        this.textPane = textPane;
        jframe = textPane.jframe;
    }
    
    /**
     * Indique si le document a �t� modifi� depuis la derni�re sauvegarde ou pas.
     */
    public boolean getModif() {
        return(modif);
    }
    
    /**
     * Sp�cifie si le document a �t� modifi� depuis la derni�re sauvegarde ou pas.
     */
    public void setModif(final boolean modif) {
        if (this.modif != modif) {
            // on �vite d'utiliser la classe JaxeFrame pour limiter les d�pendances avec les applets
            if (jframe != null && "jaxe.JaxeFrame".equals(jframe.getClass().getName()))
                jframe.getRootPane().putClientProperty("Window.documentModified", new Boolean(modif)); // pour MacOS X
            this.modif = modif;
        }
    }
    
    /**
     * Initialise un document vide
     */
    public void nouveau() {
        if (cfg == null) {
            LOG.error("nouveau() - nouveau: pas de fichier de configuration en entr�e");
            // cette erreur ne peut normalement pas arriver, donc pas de string
            // dans le ResourceBundle
            return;
        }
        fsave = null;
        furl = null;
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            final DocumentBuilder docbuilder = dbf.newDocumentBuilder();
            docbuilder.setEntityResolver(Jaxe.getEntityResolver());
            DOMdoc = docbuilder.newDocument();
        } catch (final ParserConfigurationException ex) {
            LOG.error("nouveau: ParserConfigurationException", ex);
        }
        if (cfg.getEncodage() != null)
            encodage = cfg.getEncodage();
        dom2JaxeElement = new HashMap<Node, JaxeElement>();
        final ArrayList<String> racines = cfg.listeRacines();
        if (racines.size() == 1) {
            final Element refracine = cfg.premierElementRacine();
            if (refracine == null) {
                JOptionPane.showMessageDialog(jframe, rb.getString("erreur.racineIncorrecte"),
                    rb.getString("document.Nouveau"), JOptionPane.ERROR_MESSAGE);
                rootJE = null;
            } else {
                final String typeAffichage = cfg.typeAffichageElement(refracine);
                if (!"".equals(typeAffichage))
                    rootJE = JEFactory.createJE(this, refracine, cfg.nomElement(refracine), "element", null);
                else
                    rootJE = new JEDivision(this);
                Element rootel = (Element) rootJE.nouvelElement(refracine);
                if (rootel != null) {
                    cfg.ajouterAttributsEspaces(rootel);
                    DOMdoc.appendChild(rootel);
                    textPane.debutIgnorerEdition();
                    try {
                        rootJE.creer(createPosition(0), rootel);
                    } catch (final BadLocationException ex) {
                        LOG.error("nouveau() - BadLocationException", ex);
                    }
                    textPane.finIgnorerEdition();
                    textPane.setCaretPosition(rootJE.insPosition().getOffset());
                    textPane.moveCaretPosition(rootJE.insPosition().getOffset());
                } else
                    rootJE = null;
            }
        } else
            rootJE = null;
    }

    /**
     * Initialise un document lu � partir d'une URL
     */
    public boolean lire(final URL url) {
        return(lire(url, (String)null));
    }
    
    private org.w3c.dom.Document lectureDocumentXML(final URL url) {
        org.w3c.dom.Document ddoc = null;
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            //dbf.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
            //dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // comment faire �a ? -> jaxp_feature_not_supported
            final DocumentBuilder docbuilder = dbf.newDocumentBuilder();
            
            //docbuilder.setEntityResolver(Jaxe.getEntityResolver());
            // on ignore les DTD pour ne pas gonfler le document DOM avec des valeurs d'attributs qui
            // n'�taient pas dans le document initial
            docbuilder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    if (systemId.endsWith(".dtd") || systemId.endsWith(".DTD"))
                        return(new InputSource(new StringReader("")));
                    else if (Jaxe.getEntityResolver() != null)
                        return(Jaxe.getEntityResolver().resolveEntity(publicId, systemId));
                    else
                        return(null);
                }
            });
            
            //ddoc = docbuilder.parse(url.toExternalForm());
            // le cache peut poser probl�me dans certains cas
            final URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            ddoc = docbuilder.parse(conn.getInputStream(), url.toExternalForm());
            if (ddoc.getXmlEncoding() != null) // DOM 3, Java 1.5, old xerces must not be on the classpath
                encodage = ddoc.getXmlEncoding();
        } catch (final SAXException ex) {
            String infos = rb.getString("erreur.XML") + ":\n";
            infos += ex.getMessage();
            if (ex instanceof SAXParseException)
                infos += " " + rb.getString("erreur.ALaLigne") + " " +
                    ((SAXParseException)ex).getLineNumber();
            JOptionPane.showMessageDialog(jframe, infos,
                rb.getString("document.Lecture"), JOptionPane.ERROR_MESSAGE);
            return(null);
        } catch (final IOException ex) {
            String infos = rb.getString("erreur.ES") + ":\n";
            infos += ex.getMessage();
            JOptionPane.showMessageDialog(jframe, infos,
                rb.getString("document.Lecture"), JOptionPane.ERROR_MESSAGE);
            return(null);
        } catch (final ParserConfigurationException ex) {
            LOG.error("lire: ParserConfigurationException", ex);
            return(null);
        }
        
        furl = url;
        try {
            fsave = new File(url.toURI());
        } catch (final Exception ex) {
            fsave = null;
        }
        return(ddoc);
    }
    
    /**
     * Initialise un document lu � partir d'une URL, en utilisant un fichier de config donn� par nom de fichier
     */
    public boolean lire(final URL url, final String cheminFichierCfg) {
        final org.w3c.dom.Document ddoc = lectureDocumentXML(url);
        if (ddoc == null)
            return(false);
        return(setDOMDoc(ddoc, cheminFichierCfg));
    }
    
    /**
     * Initialise un document lu � partir d'une URL, en utilisant un fichier de config donn� par URL
     */
    public boolean lire(final URL url, final URL urlFichierCfg) {
        final org.w3c.dom.Document ddoc = lectureDocumentXML(url);
        if (ddoc == null)
            return(false);
        return (setDOMDoc(ddoc, urlFichierCfg));
    }

    /**
     * Sp�cifie le document DOM de ce document Jaxe
     */
    public boolean setDOMDoc(final org.w3c.dom.Document ddoc) {
        return(setDOMDoc(ddoc, (String)null));
    }
    
    /**
     * Sp�cifie le document DOM de ce document Jaxe, en utilisant un fichier de config donn� par nom de fichier.
     * Si cheminFichierCfg est null, une config est cherch�e en fonction de la racine du document.
     */
    public boolean setDOMDoc(final org.w3c.dom.Document ddoc, final String cheminFichierCfg) {
        final Element rootel = ddoc.getDocumentElement();
        if (cheminFichierCfg == null)
            nomFichierCfg = chercherConfig(rootel);
        else
            nomFichierCfg = cheminFichierCfg;
        if (nomFichierCfg == null)
            JOptionPane.showMessageDialog(jframe,
                rb.getString("erreur.ConfigPour") + " " +
                Config.localValue(rootel.getTagName()),
                rb.getString("erreur.Erreur"), JOptionPane.ERROR_MESSAGE);
        try {
            URL urlFichierCfg;
            if (nomFichierCfg == null)
                urlFichierCfg = null;
            else
                urlFichierCfg = (new File(nomFichierCfg)).toURI().toURL();
            return(setDOMDoc(ddoc, urlFichierCfg));
        } catch (final MalformedURLException ex) {
            LOG.error("setDOMDoc: MalformedURLException: " + ex.getMessage());
            return(false);
        }
    }
    
    /**
     * Sp�cifie le document DOM de ce document Jaxe, en utilisant un fichier de config donn� par URL
     */
    public boolean setDOMDoc(final org.w3c.dom.Document ddoc, final URL urlFichierCfg) {
        DOMdoc = ddoc;
        dom2JaxeElement = new HashMap<Node, JaxeElement>();
        
        if (urlFichierCfg != null) {
            nomFichierCfg = urlFichierCfg.getPath();
            try {
                cfg = new Config(urlFichierCfg, true);
            } catch (JaxeException ex) {
                JOptionPane.showMessageDialog(jframe, ex.getMessage(),
                    rb.getString("erreur.Erreur"), JOptionPane.ERROR_MESSAGE);
                cfg = null;
            }
        } else {
            nomFichierCfg = null;
            cfg = null;
        }
        
        final Element rootel = DOMdoc.getDocumentElement();
        final Properties prefs = Preferences.getPref();
        final boolean consIndent = (prefs != null && "true".equals(prefs.getProperty("consIndent")));
        if (!consIndent)
            virerEspaces(rootel);
        
        if (rootJE != null && getLength() > 0) {
            try {
                remove(0, getLength());
            } catch (BadLocationException ex) {
                LOG.error("setDOMDoc(org.w3c.dom.Document, URL) - BadLocationException", ex);
            }
        }
        
        setModif(false);
        try {
            Position newpos = createPosition(0);
            textPane.debutIgnorerEdition();
            for (Node n = DOMdoc.getFirstChild(); n != null; n = n.getNextSibling())
                newpos = creerEnfantDuDocument(newpos, n, false);
            textPane.finIgnorerEdition();
        } catch (final BadLocationException ex) {
            LOG.error("setDOMDoc(org.w3c.dom.Document, URL) - BadLocationException", ex);
            return false;
        }
        //DefaultDocumentEvent de = new DefaultDocumentEvent(0, getLength(), DocumentEvent.EventType.CHANGE);
        //fireChangedUpdate(de);
        // marche pas !

        return true;
    }
    
    /**
     * Equivalent de JaxeElement.creerEnfant pour les noeuds directement sous le noeud document.
     * Renvoie la position apr�s l'insertion dans le document.
     */
    private Position creerEnfantDuDocument(final Position pos, final Node n, final boolean ajoutEdit) throws BadLocationException {
        JaxeElement newje = null;
        if (n.getNodeType() == Node.ELEMENT_NODE) {
            final Element rootel = (Element) n;
            if (cfg == null)
                rootJE = new JEInconnu(this);
            else {
                final Element refracine = cfg.getElementRef(rootel);
                final String typeAffichage = cfg.typeAffichageElement(refracine);
                if (!"".equals(typeAffichage))
                    rootJE = JEFactory.createJE(this, refracine, cfg.nomElement(refracine), "element", rootel);
                else
                    rootJE = new JEDivision(this);
            }
            newje = rootJE;
        } else {
            String typeNoeud;
            if (n.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)
                typeNoeud = "instruction";
            else if (n.getNodeType() == Node.COMMENT_NODE)
                typeNoeud = "commentaire";
            else
                typeNoeud = null;
            // remarque: un document DOM ne peut pas avoir de texte sous le noeud Document
            // => les espaces et retours � la ligne sont perdus � la lecture du fichier
            if (typeNoeud != null)
                newje = JEFactory.createJE(this, null, n.getNodeName(), typeNoeud, n);
        }
        if (newje != null) {
            newje.creer(pos, n);
            if (ajoutEdit)
                textPane.addEdit(new JaxeUndoableEdit(JaxeUndoableEdit.AJOUTER, newje));
            if (pos.getOffset() == 0)
                return(createPosition(newje.fin.getOffset() + 1));
        }
        return(pos);
    }
    
    /**
     * Sets the RootNode of the Document
     * 
     * @param node
     *            the Node
     * @return boolean successfull ?
     */
    public boolean setRootElement(final org.w3c.dom.Element node) {
        return setRootElement(node, node);
    }

    /**
     * Sets the RootNode of the Document with a Node that is used to search the
     * Config-File
     * 
     * @param node
     *            the Node
     * @param configNode
     *            the Node wich will be used as Config-File
     * @return boolean successfull ?
     */
    public boolean setRootElement(final org.w3c.dom.Element node,
            final org.w3c.dom.Element configNode) {
        DOMdoc = node.getOwnerDocument();
        dom2JaxeElement = new HashMap<Node, JaxeElement>();
        final Element rootel = node;
        final Properties prefs = Preferences.getPref();
        final String nomFichierCfg = chercherConfig(configNode);
        if (nomFichierCfg == null)
            LOG.error("setRootElement(org.w3c.dom.Element, org.w3c.dom.Element) - " + rb.getString("erreur.ConfigPour")
                    + " " + Config.localValue(rootel.getTagName()));

        final boolean consIndent = (prefs != null && "true".equals(prefs.getProperty("consIndent")));
        if (!consIndent)
            virerEspaces(rootel);
        
        setModif(false);
        
        try {
            Position newpos = createPosition(0);
            textPane.debutIgnorerEdition();
            creerEnfantDuDocument(newpos, rootel, false);
            textPane.finIgnorerEdition();
        } catch (final BadLocationException ex) {
            LOG.error("setRootElement(org.w3c.dom.Element, org.w3c.dom.Element) - BadLocationException", ex);
            return false;
        }
        //DefaultDocumentEvent de = new DefaultDocumentEvent(0, getLength(),
        // DocumentEvent.EventType.CHANGE);
        //fireChangedUpdate(de);
        // marche pas !

        return true;
    }

    public Node getRootElement() {
        final Node result = rootJE.noeud.cloneNode(true);
        boolean changed = false;
        do {
            changed = false;
            Node child = result.getFirstChild();
            while (child != null) {
                if (child instanceof Element) {
                    final Element refElement =  cfg.getElementRef((Element)child);
                    
                    if (refElement != null) {
                        final String typeAffichage = cfg.typeAffichageElement(refElement);
                        if ("style".equals(typeAffichage)) {
                            if (child.getNextSibling() != null) {
                                final Node next = child.getNextSibling();
                                if (next instanceof Element) {
                                    final Element refElement2 = cfg.getElementRef((Element)next);
                                    if (refElement2 != null) {
                                        final String typeAffichage2 = cfg.typeAffichageElement(refElement2);
                                        if ("style".equals(typeAffichage2)) {
                                            final Node prev = child.getPreviousSibling();
                                            changed = changed | joinNodes(child, next);
                                            if (changed) {
                                                if (prev == null) {
                                                    child = result.getFirstChild();
                                                } else {
                                                    child = prev;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (!changed) {
                                changed = changed | goDeep(child);
                            }
                        
                        } else {
                            changed = changed | goDeep(child);
                        }
                    }
                    
                } else {
                    changed = changed | goDeep(child);
                }
                child = child.getNextSibling();
            }
        } while (changed);
        return result;
    }

    private int childCount(final Node n){
        return n.getChildNodes().getLength();
    }
    
    /**
     * @param child
     * @param nextSibling
     * @return
     */
    private boolean joinNodes(final Node child, final Node nextSibling) {
        if (NodeUtils.isEqualNode(child, nextSibling)) {
            Node c = nextSibling.getFirstChild();
            while (c != null) {
                child.appendChild(c);
                c = c.getNextSibling();
            }
            nextSibling.getParentNode().removeChild(nextSibling);
            return true;
        }
        return false;
    }

    private boolean goDeep(final Node n) {
        boolean changed = false;
        Node child = n.getFirstChild();
        while (child != null) {
            if (child instanceof Element) {
                final Element refElement =  cfg.getElementRef((Element)child);
                
                if (refElement != null) {
                    final String typeAffichage = cfg.typeAffichageElement(refElement);
                    if ("style".equals(typeAffichage)) {
                        if (child.getNextSibling() != null) {
                            final Node next = child.getNextSibling();
                            if (next instanceof Element) {
                                final Element refElement2 =  cfg.getElementRef((Element)next);
                                if (refElement2 != null) {
                                    final String typeAffichage2 = cfg.typeAffichageElement(refElement2);
                                    if ("style".equals(typeAffichage2)) {
                                        final Node prev = child.getPreviousSibling();
                                        if (joinNodes(child, next)) {
                                            changed = true;
                                            if (prev == null) {
                                                child = n.getFirstChild();
                                            } else {
                                                child = prev;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!changed) {
                            changed = changed | goDeep(child);
                        }
                    
                    } else {
                        changed = changed | goDeep(child);
                    }
                }
                
            } else {
                changed = changed | goDeep(child);
            }
            child = child.getNextSibling();
        }
        return changed;
    }

    protected String chercherConfig(final Element rootel) {
        String nomFichierCfg = null;
        final File configdir = new File("config");
        final String[] liste = configdir.list();
        if (liste == null) {
            LOG.error("chercherConfig(Element) - " + rb.getString("erreur.DossierConfig"));
            return (null);
        }
        for (final String nomFichier : liste) {
            if (nomFichier.endsWith("_cfg.xml") || nomFichier.endsWith("_config.xml")) {
                final File cfgFile = new File("config" + File.separator + nomFichier);
                try {
                    final URL cfgURL = cfgFile.toURI().toURL();
                    final ArrayList<String> noms = Config.nomsElementsRacine(cfgURL);
                    if (noms.contains(Config.localValue(rootel.getTagName()))) {
                        try {
                            final Config cfgTest = new Config(cfgURL, true);
                            final String rootns = rootel.getNamespaceURI();
                            final Element refRoot = cfgTest.getElementRef(rootel);
                            if (refRoot == null)
                                continue;
                            final String cfgns = cfgTest.espaceElement(refRoot);
                            if ((rootns != null && rootns.equals(cfgns)) ||
                                    (rootns == null && (cfgns == null || "".equals(cfgns)))) {
                                nomFichierCfg = cfgFile.getPath();
                                cfg = cfgTest;
                                break;
                            }
                        } catch (JaxeException ex) {
                        }
                    }
                } catch (final MalformedURLException ex) {
                    LOG.error("JaxeDocument.chercherConfig : MalformedURLException: " + ex.getMessage(), ex);
                }
            }
        }
        return (nomFichierCfg);
    }

    // retire les espaces g�nants de cet �l�ment, et r�cursivement
    public void virerEspaces(final Element el) {
        virerEspaces(el, null, false, true);
    }
    
    private void virerEspaces(final Element el, final Element refParent, final boolean spacePreserveParent, final boolean fteParent) {
        boolean spacePreserve;
        final String xmlspace = el.getAttribute("xml:space");
        if ("preserve".equals(xmlspace))
            spacePreserve = true;
        else if ("default".equals(xmlspace))
            spacePreserve = false;
        else
            spacePreserve = spacePreserveParent;
        final Element refElement;
        if (cfg != null) {
            if (refParent == null)
                refElement = cfg.getElementRef(el);
            else
                refElement = cfg.getElementRef(el, refParent);
            if (refElement != null && "".equals(xmlspace)) {
                final Config conf = cfg.getRefConf(refElement);
                final ArrayList<Element> attributs = conf.listeAttributs(refElement);
                for (Element attref : attributs) {
                    if ("space".equals(conf.nomAttribut(attref)) && "http://www.w3.org/XML/1998/namespace".equals(conf.espaceAttribut(attref))) {
                        final String defaut = conf.valeurParDefaut(attref);
                        if ("preserve".equals(defaut))
                            spacePreserve = true;
                        else if ("default".equals(defaut))
                            spacePreserve = false;
                        break;
                    }
                }
            }
        } else
            refElement = null;
        final boolean fte = isFirstTextElement(el, refElement, refParent, fteParent);
        for (Node n = el.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE)
                virerEspaces((Element) n, refElement, spacePreserve, fte);
            else if (!spacePreserve && n.getNodeType() == Node.TEXT_NODE) {
                final StringBuilder sBuilder = new StringBuilder(n.getNodeValue());
                
                // on ne retire pas les blancs s'il n'y a que du blanc dans l'�l�ment
                if (n.getNextSibling() == null && n.getPreviousSibling() == null && "".equals(sBuilder.toString().trim()))
                    break;
                
                if (fte && n.getParentNode().getFirstChild() == n && refParent != null) {
                    // retire espaces au d�but si le texte est au d�but de l'�l�ment
                    int ifin = 0;
                    while (ifin < sBuilder.length() && (sBuilder.charAt(ifin) == ' ' || sBuilder.charAt(ifin) == '\t'))
                        ifin++;
                    if (ifin > 0)
                        sBuilder.delete(0, ifin);
                }
                
                // retire les espaces apr�s les retours � la ligne
                int idebut = sBuilder.indexOf("\n ");
                int idebuttab = sBuilder.indexOf("\n\t");
                if (idebuttab != -1 && (idebut == -1 || idebuttab < idebut))
                    idebut = idebuttab;
                while (idebut != -1) {
                    int ifin = idebut;
                    while (ifin + 1 < sBuilder.length()
                            && (sBuilder.charAt(ifin + 1) == ' ' || sBuilder.charAt(ifin + 1) == '\t'))
                        ifin++;
                    sBuilder.delete(idebut + 1, ifin + 1);
                    idebut = sBuilder.indexOf("\n ");
                    idebuttab = sBuilder.indexOf("\n\t");
                    if (idebuttab != -1 && (idebut == -1 || idebuttab < idebut))
                        idebut = idebuttab;
                }
                
                // condense les espaces partout
                idebut = sBuilder.indexOf("  ");
                while (idebut != -1) {
                    int ifin = idebut;
                    while (ifin + 1 < sBuilder.length() && sBuilder.charAt(ifin + 1) == ' ')
                        ifin++;
                    sBuilder.delete(idebut, ifin);
                    idebut = sBuilder.indexOf("  ");
                }
                
                // mise � jour du noeud
                if (sBuilder.length() == 0) {
                    Node n2 = n.getPreviousSibling();
                    el.removeChild(n);
                    if (n2 == null)
                        n2 = el.getFirstChild();
                    n = n2;
                    if (n == null)
                        break;
                } else
                    n.setNodeValue(sBuilder.toString());
            }
        }
    }
    
    // renvoie false s'il ne faut pas retirer les espaces au d�but de l'�l�ment
    private boolean isFirstTextElement(final Element el, final Element refEl, final Element refParent, final boolean fteParent) {
        if (refEl == null)
            return true;
        if (refParent == null || !cfg.contientDuTexte(refEl) || !cfg.contientDuTexte(refParent))
            return true;
        Node prevNode = el.getPreviousSibling();
        while (prevNode != null) {
            if (prevNode.getNodeType() == Node.TEXT_NODE) {
                final String prevText = prevNode.getNodeValue();
                if (!(prevText.endsWith(" ") || prevText.endsWith("\n")))
                    return false;
                return true;
            } else if (prevNode.getNodeType() == Node.ELEMENT_NODE) {
                final Node lc = prevNode.getLastChild();
                if (lc != null && lc.getNodeType() == Node.TEXT_NODE) {
                    final String prevText = lc.getNodeValue();
                    if (!(prevText.endsWith(" ") || prevText.endsWith("\n")))
                        return false;
                }
                return true;
            }
            prevNode = prevNode.getPreviousSibling();
        }
        if (prevNode == null && refParent != null)
            return fteParent;
        return true;
    }

    
    public void sendToWriter(final Writer destination) {
        sendToWriter(destination, false);
    }
    
    public void sendToWriter(final Writer destination, final boolean indenter) {
        org.w3c.dom.Document document = null;
        if (indenter) {
            document = (org.w3c.dom.Document)DOMdoc.cloneNode(true);
            ajouterIndentations(document, 2);
        } else
            document = DOMdoc;
        try {
            final DOMSource domSource = new DOMSource(document);
            final StreamResult streamResult = new StreamResult(destination);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.METHOD, "xml");
            serializer.setOutputProperty(OutputKeys.ENCODING, encodage);
            serializer.setOutputProperty(OutputKeys.INDENT, "no");
            if (cfg != null) {
                if (cfg.getPublicId() != null)
                    serializer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, cfg.getPublicId());
                if (cfg.getSystemId() != null)
                    serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, cfg.getSystemId());
            }
            serializer.transform(domSource, streamResult);
        } catch (final TransformerConfigurationException ex) {
            LOG.error("sendToWriter: TransformerConfigurationException", ex);
        } catch (final TransformerException ex) {
            LOG.error("sendToWriter: TransformerException", ex);
        }
    }
    
    class ReaderThread extends Thread {
        PipedWriter pipeout;
        boolean indenter;
        public ReaderThread(final PipedWriter pipeout, final boolean indenter) {
            this.pipeout = pipeout;
            this.indenter = indenter;
        }
        @Override
        public void run() {
            sendToWriter(pipeout, indenter);
            try {
                pipeout.close();
            } catch (final IOException ex) {
                LOG.error("ReaderThread: pipeout.close", ex);
            }
        }
    }
    
    public Reader getReader() throws IOException {
        return(getReader(false));
    }
    
    public Reader getReader(final boolean indenter) throws IOException {
        final PipedWriter pipeout = new PipedWriter();
        final PipedReader pipein = new PipedReader(pipeout);
        final ReaderThread rt = new ReaderThread(pipeout, indenter);
        rt.start();
        return pipein;
    }
    
    public void ecrire(final File f) throws IOException {
        final FileOutputStream fos = new FileOutputStream(f);
        final Writer fw = new OutputStreamWriter(fos, encodage);
        final Properties prefs = Preferences.getPref();
        final boolean prefIndenter = (prefs != null && "true".equals(prefs.getProperty("indenter")));
        sendToWriter(fw, prefIndenter);
        fw.close();
        fos.close();
        try {
            furl = f.toURI().toURL();
        } catch (final MalformedURLException ex) {
            LOG.error("ecrire(File) - MalformedURLException", ex);
            furl = null;
        }
        fsave = f;
        setModif(false);
    }
    
    private void ajouterIndentations(final Element el, final Element refParent, final boolean spacePreserveParent, final int nbEspaces, final int nbIndent) {
        final String espaces = "                                                                                                                                ";
        final int maxIndent = espaces.length() / nbEspaces;
        boolean spacePreserve;
        final String xmlspace = el.getAttribute("xml:space");
        if ("preserve".equals(xmlspace))
            spacePreserve = true;
        else if ("default".equals(xmlspace))
            spacePreserve = false;
        else
            spacePreserve = spacePreserveParent;
        final Element refElement;
        if (cfg != null) {
            if (refParent == null)
                refElement = cfg.getElementRef(el);
            else
                refElement = cfg.getElementRef(el, refParent);
            if (refElement != null && "".equals(xmlspace)) {
                final Config conf = cfg.getRefConf(refElement);
                final ArrayList<Element> attributs = conf.listeAttributs(refElement);
                for (Element attref : attributs) {
                    if ("space".equals(conf.nomAttribut(attref)) && "http://www.w3.org/XML/1998/namespace".equals(conf.espaceAttribut(attref))) {
                        final String defaut = conf.valeurParDefaut(attref);
                        if ("preserve".equals(defaut))
                            spacePreserve = true;
                        else if ("default".equals(defaut))
                            spacePreserve = false;
                        break;
                    }
                }
            }
        } else
            refElement = null;
        for (Node n = el.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() == Node.ELEMENT_NODE)
                ajouterIndentations((Element)n, refElement, spacePreserve, nbEspaces, nbIndent >= maxIndent ? nbIndent : nbIndent + 1);
            else if (!spacePreserve && n.getNodeType() == Node.TEXT_NODE) {
                final String v = n.getNodeValue();
                if (v.contains("\n")) {
                    String v2;
                    if (n.getNextSibling() != null)
                        v2 = v.replace("\n", "\n" + espaces.substring(0, nbEspaces*nbIndent));
                    else {
                        boolean dernier = true;
                        final StringBuilder bv = new StringBuilder(v);
                        for (int i=v.length()-1; i>=0; i--) {
                            int pos = v.lastIndexOf("\n", i);
                            if (pos == -1)
                                break;
                            if (!dernier)
                                bv.insert(pos+1, espaces.substring(0, nbEspaces*nbIndent));
                            else {
                                dernier = false;
                                bv.insert(pos+1, espaces.substring(0, nbEspaces*(nbIndent-1)));
                            }
                            i = pos;
                        }
                        v2 = bv.toString();
                    }
                    n.setNodeValue(v2);
                }
            }
            // indentation des commentaires ? actuellement les espaces ne sont pas retir�s dans les commentaires avec virerEspaces
        }
    }
    
    /**
     * Ajoute des indentations au document pass� en param�tre, avec le nombre d'espaces indiqu� pour chaque indentation.
     */
    public void ajouterIndentations(final org.w3c.dom.Document doc, final int nbEspaces) {
        ajouterIndentations(doc.getDocumentElement(), null, false, nbEspaces, 1);
    }
    
    public String getPathAsString(final int p) {
        if (rootJE == null || rootJE.debut == null || rootJE.fin == null || p < rootJE.debut.getOffset() || p > rootJE.fin.getOffset()) {
            for (Node n = DOMdoc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n.getNodeType() == Node.COMMENT_NODE || n.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                    final JaxeElement je = getElementForNode(n);
                    if (je != null && je.debut != null && je.debut.getOffset() <= p && je.fin != null && je.fin.getOffset() >= p) {
                        if (n.getNodeType() == Node.COMMENT_NODE)
                            return("commentaire");
                        else
                            return("instruction");
                    }
                }
            }
            return(null);
        }
        final String chemin = rootJE.cheminA(p);
        return (chemin);
    }

    public void mettreAJourDOM() {
        rootJE.mettreAJourDOM();
    }

    public JaxeElement elementA(final int pos) {
        for (Node n = DOMdoc.getFirstChild(); n != null; n = n.getNextSibling()) {
            final short type = n.getNodeType();
            if (type == Node.ELEMENT_NODE || type == Node.TEXT_NODE || type == Node.PROCESSING_INSTRUCTION_NODE ||
                    type == Node.COMMENT_NODE || type == Node.CDATA_SECTION_NODE) {
                final JaxeElement je = getElementForNode(n);
                if (je != null) {
                    final JaxeElement nje = je.elementA(pos);
                    if (nje != null)
                        return nje;
                }
            }
        }
        return(null);
    }
    
    /**
     * Renvoit les �l�ments se trouvant dans la zone du texte indiqu�e (de dpos � fpos inclu)
     */
    public ArrayList<JaxeElement> elementsDans(final int dpos, final int fpos) {
        final ArrayList<JaxeElement> l = new ArrayList<JaxeElement>();
        for (Node n = DOMdoc.getFirstChild(); n != null; n = n.getNextSibling()) {
            final short type = n.getNodeType();
            if (type == Node.ELEMENT_NODE || type == Node.PROCESSING_INSTRUCTION_NODE || type == Node.COMMENT_NODE) {
                final JaxeElement je = getElementForNode(n);
                if (je != null)
                    l.addAll(je.elementsDans(dpos, fpos));
            }
        }
        return l;
    }
    
    /**
     * Copie r�cursive d'un �l�ment en se limitant � l'intervalle [debut,fin[.
     * L'�l�ment doit �tre enti�rement dans l'intervalle, sauf si c'est un texte ou un style.
     * Renvoie null en cas d'erreur.
     */
    private Node copierNoeud(final JaxeElement je, final int debut, final int fin) {
        if (je.debut.getOffset() > fin - 1 || je.fin.getOffset() < debut) {
            LOG.error("JaxeDocument.copierNoeud: pb intervalle");
            return(null);
        }
        if (je.debut.getOffset() >= debut && (je.fin.getOffset() <= fin - 1 || (je instanceof JESwing && je.fin.getOffset() == fin)))
            return(je.noeud.cloneNode(true));
        if (je instanceof JETexte) {
            String texte = je.noeud.getNodeValue();
            texte = texte.substring(Math.max(0, debut - je.debut.getOffset()),
                Math.min(texte.length(), texte.length() - (je.fin.getOffset() - (fin - 1))));
            final Node tn = DOMdoc.createTextNode(texte);
            return(tn);
        } else if (je instanceof JEStyle) {
            final Node sn = je.noeud.cloneNode(false);
            for (Node n=je.noeud.getFirstChild(); n != null; n = n.getNextSibling()) {
                final JaxeElement jn = getElementForNode(n);
                if (jn.debut.getOffset() <= fin - 1 && jn.fin.getOffset() >= debut) {
                    final Node cn = copierNoeud(jn, debut, fin);
                    if (cn != null)
                        sn.appendChild(cn);
                }
            }
            return(sn);
        } else {
            LOG.error("JaxeDocument.copierNoeud: un �l�ment (hors texte et style) non complet ne peux pas �tre copi�: " + je);
            return(null);
        }
    }
    
    /**
     * Cr�ation d'un fragment � partir des enfants d'un �l�ment compris dans l'intervalle [debut,fin[.
     * Renvoie null s'il n'y a aucun �l�ment dans l'intervalle.
     */
    private DocumentFragment copierFragment(final Node nparent, final int debut, final int fin) {
        final DocumentFragment frag = DOMdoc.createDocumentFragment();
        for (Node n = nparent.getFirstChild(); n != null; n = n.getNextSibling()) {
            final JaxeElement jn = getElementForNode(n); // null pour les zones de texte entre les lignes d'un tableau
            if (jn != null && jn.debut.getOffset() <= fin - 1 && jn.fin.getOffset() >= debut) {
                final Node cn = copierNoeud(jn, debut, fin);
                if (cn != null)
                    frag.appendChild(cn);
            }
        }
        if (frag.getFirstChild() == null)
            return(null);
        return(frag);
    }
    
    public DocumentFragment copier(final int debut, final int fin) {
        JaxeElement firstel = elementA(debut);
        if (firstel == null) {
            Toolkit.getDefaultToolkit().beep();
            return null;
        }
        firstel = elementA(debut);
        while (firstel.debut.getOffset() == debut && firstel.getParent() instanceof JESwing &&
                firstel.getParent().debut.getOffset() == debut && firstel.getParent().fin.getOffset() <= fin)
            firstel = firstel.getParent();
        JaxeElement p1 = firstel;
        if (p1 instanceof JETexte || p1 instanceof JEStyle || p1.debut.getOffset() == debut)
            p1 = p1.getParent();
        while (p1 instanceof JEStyle && p1.getParent() != null)
            p1 = p1.getParent();
        JaxeElement lastel = elementA(fin - 1);
        if (lastel == null) {
            Toolkit.getDefaultToolkit().beep();
            return null;
        }
        if (lastel.fin.getOffset() == fin-1 && lastel.getParent() instanceof JESwing &&
                lastel.getParent().debut.getOffset() >= debut && lastel.getParent().fin.getOffset() == fin)
            lastel = lastel.getParent();
        if (lastel.fin.getOffset() == fin && lastel.getParent() instanceof JESwing &&
                lastel.getParent().debut.getOffset() >= debut && lastel.getParent().fin.getOffset() == fin)
            lastel = lastel.getParent();
        while (lastel.fin.getOffset() == fin-1 &&
                (lastel.getParent() instanceof JESwing || lastel.getParent() instanceof JETableTexte) &&
                lastel.getParent().debut.getOffset() >= debut && lastel.getParent().fin.getOffset() == fin-1)
            lastel = lastel.getParent();
        JaxeElement p2 = lastel;
        if (p2 instanceof JETexte || p2 instanceof JEStyle || p2.fin.getOffset() == fin - 1 ||
                (p2 instanceof JESwing && p2.fin.getOffset() == fin))
            p2 = p2.getParent();
        while (p2 instanceof JEStyle && p2.getParent() != null)
            p2 = p2.getParent();
        
        if (p1 == null && p2 == null && firstel.noeud.getParentNode() == DOMdoc)
            return(copierFragment(DOMdoc, debut, fin));
        
        if (p1 != p2 || p1 == null)
            return null;
        
        if (firstel == lastel && firstel.getClass().getName().equals("jaxe.elements.JETableTexte$JESwingTD")) {
            // on ne copie pas la cellule enti�re si juste son contenu est s�lectionn�
            p1 = firstel;
        }
        final DocumentFragment frag = copierFragment(p1.noeud, debut, fin);
//        removeProcessingInstructions(frag);
        return frag;
    }
    
    @Deprecated
    protected Node removeProcessingInstructions(final Node n) {
        if (n == null) return null;
        Node child = n.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                final Node prev = child.getPreviousSibling();
                child.getParentNode().removeChild(child);
                if (prev != null) {
                    child = prev;
                } else {
                    child = n.getFirstChild();
                    continue;
                }
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeProcessingInstructions(child);
            }
            child = child.getNextSibling();
        }
        return n;
    }

    /**
     * Teste si l'insertion d'un fragment est autoris�e sous un certain �l�ment
     * parent � la position pos. Si elle n'est pas autoris�e, affiche un message
     * d'erreur et renvoit false. Sinon renvoit true.
     */
    public boolean testerInsertionFragment(final DocumentFragment frag,
            final JaxeElement parent, final Position pos) {
        if (cfg != null) {
            if (parent == null) {
                boolean ok = true;
                int nbel = 0;
                for (Node n=frag.getFirstChild(); n != null; n=n.getNextSibling()) {
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        final String nomElement = n.getNodeName();
                        Config conf = cfg.getElementConf((Element) n);
                        if (conf == null)
                            conf = cfg;
                        final Element refElement = conf.referenceElement(nomElement);
                        final ArrayList<Element> racines = cfg.listeElementsRacines();
                        if (refElement == null) {
                            LOG.error("testerInsertionFragment : pas de r�f�rence pour " + nomElement);
                            ok = false;
                        } else if (!racines.contains(refElement))
                            ok = false;
                        nbel++;
                    } else if (n.getNodeType() == Node.TEXT_NODE || n.getNodeType() == Node.CDATA_SECTION_NODE)
                        ok = false;
                }
                if (nbel > 1 || (nbel == 1 && rootJE != null))
                    ok = false;
                if (!ok) {
                    JOptionPane.showMessageDialog(jframe, rb.getString("insertion.SousRacine"),
                        rb.getString("insertion.InsertionBalise"), JOptionPane.ERROR_MESSAGE);
                }
                return(ok);
            } else {
                Element parentref = parent.refElement;
                for (Node n=frag.getFirstChild(); n != null; n=n.getNextSibling()) {
                    if (n.getNodeType() == Node.TEXT_NODE && !"".equals(n.getNodeValue().trim()) &&
                            parentref != null && !cfg.contientDuTexte(parentref)) {
                        final String infos = rb.getString("erreur.InsertionInterdite") + " " +
                            parent.noeud.getNodeName();
                        JOptionPane.showMessageDialog(jframe, infos,
                            rb.getString("document.Insertion"), JOptionPane.ERROR_MESSAGE);
                        return (false);
                    } else if (n.getNodeType() == Node.ELEMENT_NODE) {
                        final String nomElement = n.getNodeName();
                        Config conf = cfg.getElementConf((Element) n);
                        if (conf == null)
                            conf = cfg;
                        final Element refElement = conf.referenceElement(nomElement);
                        if (refElement == null)
                            LOG.error("testerInsertionFragment : pas de r�f�rence pour " + nomElement);
                        parentref = null;
                        Element parentn = (Element) parent.noeud;
                        final Config pconf = cfg.getElementConf(parentn);
                        if (pconf != null && pconf != conf)
                            parentn = cfg.chercheParentConfig(parentn, conf);
                        if (parentn != null)
                            parentref = dom2JaxeElement.get(parentn).refElement;
                        if (parentref != null && !conf.estSousElement(parentref, nomElement)) {
                            gestionErreurs.enfantInterditSousParent(parent, refElement);
                            return (false);
                        }
                        if (!cfg.insertionPossible(parent, pos.getOffset(), pos.getOffset(), refElement)) {
                            String expr;
                            if (parentref == null)
                                expr = "";
                            else
                                expr = cfg.expressionReguliere(parentref);
                            gestionErreurs.insertionImpossible(expr, parent, refElement);
                            return (false);
                        }
                    }
                }
            }
        }
        return (true);
    }
    
    /** pour coller du XML */
    public boolean coller(final Object pp, final Position pos) {
        if (!(pp instanceof DocumentFragment)) return false;
        final DocumentFragment frag = (DocumentFragment) (((DocumentFragment) pp).cloneNode(true));
        
        return coller(frag, pos, true);
    }
    
    /**
     * Colle un fragment XML dans le document � la position pos
     * @param pos
     * @param frag
     */
    public boolean coller(DocumentFragment frag, Position pos, final boolean event) {
        JaxeElement parent = null;
        if (rootJE != null)
            parent = elementA(pos.getOffset());
        if (parent != null && parent.debut.getOffset() == pos.getOffset() && !(parent instanceof JESwing))
            parent = parent.getParent() ;

        textPane.debutEditionSpeciale(JaxeResourceBundle.getRB().getString("menus.Coller"), false);
        
        if (parent != null && parent.noeud.getNodeType() == Node.TEXT_NODE || parent instanceof JEStyle) {
            final JaxeElement je1 = parent;
            parent = parent.getParent();
            if (pos.getOffset() > je1.debut.getOffset()
                    && pos.getOffset() <= je1.fin.getOffset()) {
                // couper la zone de texte en 2
                final JaxeElement je2 = je1.couper(pos);
            }
        }

        if (!testerInsertionFragment(frag, parent, pos)) {
            textPane.finEditionSpeciale();
//            textPane.undo();
            return false;
        }
        if (event)
            pos = firePrepareElementAddEvent(pos);

        if (DOMdoc != frag.getOwnerDocument())
            frag = (DocumentFragment) DOMdoc.importNode(frag, true);
        
        final ArrayList<Node> nl = new ArrayList<Node>();
        for (Node n = frag.getFirstChild(); n != null; n = n.getNextSibling())
            nl.add(n);

        if (parent == null) {
            // si on colle des noeuds directement sous le noeud document (comme la racine)
            JaxeElement.insererDOM(this, pos, frag);
            textPane.debutEditionSpeciale(JaxeResourceBundle.getRB().getString("menus.Coller"), true);
            try {
                for (final Node n : nl)
                    pos = creerEnfantDuDocument(pos, n, true);
            } catch (final BadLocationException ex) {
                LOG.error("coller - BadLocationException", ex);
            }
            textPane.finEditionSpeciale();
            textPane.finEditionSpeciale();
            setModif(true);
            textPane.miseAJourArbre();
            return(true);
        }
        
        parent.insererDOM(pos, frag);
        textPane.debutEditionSpeciale(JaxeResourceBundle.getRB().getString("menus.Coller"), true);
        JaxeElement last = null;
        for (final Node n : nl) {
            // creerEnfant modifie le ptr de fin, ce qui est utile � la cr�ation
            // du doc, mais pas ici
            final Position sfin = parent.fin;
            parent.creerEnfant(pos, n);
            parent.fin = sfin;
            final JaxeElement newje = getElementForNode(n);
            
            // on corrige la position du parent, qui peut �tre chang�e apr�s creerEnfant si c'est un JESwing
            JaxeElement testparent = parent;
            while (testparent instanceof JESwing && testparent.debut.getOffset() > newje.debut.getOffset()) {
                try {
                    testparent.debut = createPosition(newje.debut.getOffset());
                } catch (final BadLocationException ex) {
                    LOG.error("coller(DocumentFragment, Position, boolean) - BadLocationException", ex);
                }
                testparent = testparent.getParent();
            }
            
            if (newje != null)
                textPane.addEdit(new JaxeUndoableEdit(JaxeUndoableEdit.AJOUTER, newje));
            last = newje;
        }
        if (event)
            pos = fireElementAddedEvent(new JaxeEditEvent(this, last), pos);
        textPane.finEditionSpeciale();
        textPane.finEditionSpeciale();
        parent.regrouperTextes();
        parent.majValidite();
        setModif(true);
        textPane.miseAJourArbre();
        return true;
    }

    @Deprecated
    public void coller(final JTextComponent target) {
        LOG.error("coller(JTextComponent)");
    }

    @Deprecated
    public String pp2string(final Object pp) {
        if (!(pp instanceof DocumentFragment))
            return null;
        final DocumentFragment frag = (DocumentFragment) pp;
        return(DOMVersXML(frag));
    }
    
    public static String DOMVersXML(final Node xmldoc) {
        try {
            final DOMSource domSource = new DOMSource(xmldoc);
            final StringWriter sw = new StringWriter();
            final StreamResult streamResult = new StreamResult(sw);
            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer serializer = tf.newTransformer();
            //serializer.setOutputProperty(OutputKeys.ENCODING, encodage);
            serializer.setOutputProperty(OutputKeys.INDENT, "no");
            serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            serializer.transform(domSource, streamResult);
            return(sw.toString());
        } catch (final TransformerConfigurationException ex) {
            LOG.error("DOMVersXML: TransformerConfigurationException", ex);
            return(null);
        } catch (final TransformerException ex) {
            LOG.error("DOMVersXML: TransformerException", ex);
            return(null);
        }
    }
    
    protected void removeText(final int offs, final int len, final boolean event) throws BadLocationException {
        final String str = getText(offs, len);
//        textPane.debutEditionSpeciale(JaxeResourceBundle.getRB().getString("annulation.AnnulerSuppression"), false);
        final JaxeUndoableEdit jedit = new JaxeUndoableEdit(
                JaxeUndoableEdit.SUPPRIMER, this, str, offs);
        jedit.doit();
        if (event) fireTextRemovedEvent(new JaxeEditEvent(this, offs, str));
//        textPane.finEditionSpeciale();
    }

    @Override
    public void remove(final int offs, final int len) throws BadLocationException {
        remove(offs, len, true);
    }

    /**
     * @param offs
     * @param len
     * @param event
     * @throws BadLocationException
     */
    public void remove(int offs, final int len, final boolean event) throws BadLocationException {
        if (textPane.getIgnorerEdition()) {
            super.remove(offs, len);
            return;
        }
        setModif(true);
        final JaxeElement firstel = elementA(offs);
        final JaxeElement lastel = elementA(offs + len - 1);
        if (firstel == lastel) {
            JaxeElement je = firstel;

            boolean avirer = false;
            if (je != null) {
            // si un JComponent est effac�, on efface tout le JaxeElement
                final ArrayList<Position> compos = je.getComponentPositions();
                for (final Position p : compos) {
                    final int cp = p.getOffset();
                    if (cp >= offs && cp < offs + len) {
                        avirer = true;
                        break;
                    }
                }
                // on efface aussi le JaxeElement s'il est enti�rement dans la
                // s�lection
                if (je.debut.getOffset() >= offs
                        && je.fin.getOffset() < offs + len) avirer = true;
                // ou si c'est un �l�ment JESwing dont on efface le dernier
                // caract�re
                if (je instanceof JESwing
                        && offs + len - 1 >= je.fin.getOffset()
                        && offs <= je.fin.getOffset()) {
                    while (je.getParent() != null
                            && je.getParent().fin.getOffset() == je.fin
                                    .getOffset())
                        je = je.getParent();
                    avirer = true;
                }
                if (avirer) {
                    if (!je.getEffacementAutorise()) { // SI c'est autoris� !
                        Toolkit.getDefaultToolkit().beep();
                        return;
                    }
                    if (je instanceof JESwing) {
                        JaxeElement parent = je;
                        while (parent != null && parent instanceof JESwing)
                            parent = parent.getParent();
                        // on efface tout le parent si c'est aussi un JESwing
                        je = parent;
                    }
                }
            }
            if (avirer) {
//                textPane.debutEditionSpeciale(JaxeResourceBundle.getRB()
//                        .getString("annulation.Supprimer"), false);
                // effacer aussi le parent s'il est exactement � la m�me position
                if (je.getParent() != null &&
                        je.getParent().debut.getOffset() == je.debut.getOffset() &&
                        je.getParent().fin.getOffset() == je.fin.getOffset())
                    je = je.getParent();
                if (je.debut.getOffset() < offs)
                    offs = je.debut.getOffset()+1;
                final JaxeUndoableEdit e = new JaxeUndoableEdit(
                        JaxeUndoableEdit.SUPPRIMER, je);
                // on ne peut pas faire e.doit() tout de suite parce-que les
                // autres listeners doivent �tre invoqu�s avant la modif
                //SwingUtilities.invokeLater(new ChangeRunnable(e));
                // invoquer plus tard pause probl�me quand on veut faire un
                // insertString juste apr�s: du coup il est fait avant...
                // finalement, �a a l'air de marcher avec e.doit(), alors on essaie...
                // maintenant �a plante avec la JVM d'Apple, on essaye de contourner le probl�me...
                appleBugWorkaround(je.debut.getOffset());
                e.doit();
                final JaxeEditEvent jee = new JaxeEditEvent(this, je);
                if (event) fireElementRemovedEvent(jee);
                if (jee.isConsumed()) textPane.setCaretPosition(offs);
//                textPane.finEditionSpeciale();
                textPane.miseAJourArbre();
            } else {
                /*if (je != null) {  retir�: fait dans JaxeUndoableEdit.effacer()
                    int finoff = je.fin.getOffset();
                    if (offs + len - 1 == finoff)
                        je.fin = createPosition(finoff - 1);
                }*/
                if (je instanceof JETexte || (je.debut.getOffset() == offs && !(je instanceof JESwing)))
                    je = je.getParent();
                if (je != null && !je.getEditionAutorisee()) {
                    gestionErreurs.texteInterdit(je);
                    return;
                }
                removeText(offs, len, event);
            }
        } else {
            //SwingUtilities.invokeLater(new SupRunnable(offs, len));
            // pour faire toutes les modifs (texte et �l�ment) dans l'ordre, on est oblig� de tout faire plus tard
            // tentative d'appel direct (c'est important pour ActionInsertionBalise,
            // qui doit ins�rer des �l�ments apr�s en avoir supprim�)
            // question: sous quel environnement cela ne marche pas ?
            remove2(offs, len, event);
            textPane.miseAJourArbre();
        }
        _ignorer = false;
    }
    
    /**
     * Parfois un remove() provoque l'appel de DefaultCaret.setVisible() par apple.laf.AquaCaret dans le thread d'�v�nements.
     * Le r�sultat est une boucle infinie dans javax.swing.text.FlowView$FlowStrategy.layoutRow().
     * Cette bidouille d�place le curseur � l'avance.
     */
    private void appleBugWorkaround(final int dot) {
        if (System.getProperty("os.name").startsWith("Mac OS"))
            textPane.getCaret().setDot(dot);
    }
    
    public void remove2(final int offs, final int len, final boolean event) {
        appleBugWorkaround(offs);
        try {
            JaxeElement firstel = elementA(offs);
            if (firstel == null)
                return;
            while (firstel.getParent() instanceof JEStyle)
                firstel = firstel.getParent();
            final JaxeElement parent = firstel.getParent();
            if ((firstel instanceof JEStyle || firstel instanceof JETexte) && firstel.debut.getOffset() < offs)
                firstel = firstel.couper(textPane.getDocument().createPosition(offs));
            JaxeElement lastel = elementA(offs + len - 1);
            while (lastel != null && lastel.getParent() instanceof JEStyle)
                lastel = lastel.getParent();
            if ((lastel instanceof JEStyle || lastel instanceof JETexte) && lastel.fin.getOffset() >= offs + len)
                lastel.couper(textPane.getDocument().createPosition(offs + len));
            final ArrayList<JaxeElement> l = elementsDans(offs, offs + len - 1);
            if (l.size() == 1 && l.get(0) instanceof JESwing) {
                // cas des cellules de tableaux, on souhaite effacer tout l'int�rieur mais pas la cellule
                final JaxeElement pswing = l.get(0);
                if (pswing.debut.getOffset() == offs && pswing.fin.getOffset() == offs+len &&
                        !pswing.getEffacementAutorise()) {
                    l.remove(0);
                    for (Node n = pswing.noeud.getFirstChild(); n != null; n = n.getNextSibling())
                        l.add(dom2JaxeElement.get(n));
                }
            }
            for (final JaxeElement je : l) {
                if (!_ignorer && !je.getEffacementAutorise() && (je.getParent() == null ||
                        !l.contains(je.getParent()) || !je.getParent().getEffacementAutorise())) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            textPane.debutEditionSpeciale(JaxeResourceBundle.getRB().getString("annulation.Supprimer"), true);
            for (final JaxeElement je : l) {
                // les textes peuvent �tre fusionn�s et je.getParent devient null
                // -> utilisation de removeText
                if (je instanceof JETexte)
                    removeText(je.debut.getOffset(), je.fin.getOffset() - je.debut.getOffset() + 1, event);
                else {
                    // option regrouper=false pour �viter les fusions avec JEStyle
                    final JaxeUndoableEdit e = new JaxeUndoableEdit(JaxeUndoableEdit.SUPPRIMER, je, false);
                    e.doit();
                    if (event)
                        fireElementRemovedEvent(new JaxeEditEvent(this, je));
                }
            }
            if (parent != null)
                parent.regrouperTextes();
            textPane.finEditionSpeciale();
        } catch (final BadLocationException ex) {
            LOG.error("remove2(int, int, boolean) - BadLocationException", ex);
        }
    }
    
    /**
     * Ignorer l'interdiction d'effacer des �l�ments. Utilis� par ActionInsertionBalise
     * quand l'utilisateur annule une insertion.
     */
    public void enableIgnore() {
        _ignorer = true;
    }
    
    /*
     * class ChangeRunnable implements Runnable { JaxeUndoableEdit edit; public
     * ChangeRunnable(JaxeUndoableEdit e) { this.edit = e; } public void run() {
     * edit.doit(); textPane.miseAJourArbre(); } }
     */
    @Override
    public void insertString(final int offset, final String str, final AttributeSet a)
    throws BadLocationException {
        insertString(offset, str, a, true);
    }

    /*class SupRunnable implements Runnable {
        int offs;
        int len;
        public SupRunnable(int offs, int len) {
            this.offs = offs;
            this.len = len;
        }
    public void run() {
            remove2(offs, len);
            textPane.miseAJourArbre();
        }
    }*/

    public void insertString(final int offset, String str, final AttributeSet a, final boolean event)
            throws BadLocationException {
        if (textPane.getIgnorerEdition()) {
            super.insertString(offset, str, a);
            return;
        }
        setModif(true);

        final int debut = textPane.getSelectionStart();
        final int fin = textPane.getSelectionEnd();
        /*
        Test retir� parce-qu'il n'y a plus d'appel � invokeLater dans remove.
        On peut maintenant faire des insertions quand il y a une s�lection.
        Ca corrige un bug dans la correction d'orthographe (Jazzy faisait parfois des insertions
        avec insertString alors qu'il y avait une s�lection)
        if (debut != fin) {
            // un appel � remove est g�n�r� automatiquement *apr�s* l'appel �
            // insertString !
            // (probablement � cause du invokeLater dans remove)
            // on ne peut donc pas faire d'insertion quand il y a une
            // s�lection...
            return;
        }
        */
        
        JaxeElement je = elementA(offset);
        if (je == null)
            return;// le texte n'est pas autoris� en-dehors de la racine
        if (je instanceof JETexte || (je.debut.getOffset() == offset && !(je instanceof JESwing)))
            je = je.getParent();
        if (je == null)
            return;
        if (cfg != null) {
            Element jeref;
            if (je == null || !(je.noeud instanceof Element))
                jeref = null;
            else
                jeref = je.refElement;
            if (jeref != null && ((!cfg.contientDuTexte(jeref) && !"".equals(str.trim())) ||
                    !je.getEditionAutorisee())) {
                gestionErreurs.texteInterdit(je);
                return;
            }
        }
        
        //super.insertString(offset, str, a);
        final Properties prefs = Preferences.getPref();
        final boolean consIndent = (prefs != null && "true".equals(prefs.getProperty("consIndent")));
        if (consIndent && "\n".equals(str)) {
            // ajout d'un espace comme celui de la ligne pr�c�dente en d�but de ligne
            int i1 = offset - 255;
            if (i1 < 0)
                i1 = 0;
            String extrait = textPane.getText(i1, offset - i1);
            i1 = extrait.lastIndexOf('\n');
            if (i1 != -1) {
                extrait = extrait.substring(i1 + 1);
                for (i1 = 0; i1 < extrait.length()
                        && (extrait.charAt(i1) == ' ' || extrait.charAt(i1) == '\t'); i1++)
                    ;
                str += extrait.substring(0, i1);
            }
        }
//        if (event) textPane.debutEditionSpeciale(JaxeResourceBundle.getRB().getString(
//        "annulation.AnnulerAjout"), false);
        final JaxeUndoableEdit jedit = new JaxeUndoableEdit(JaxeUndoableEdit.AJOUTER,
            this, str, offset);
        jedit.doit();
        if (event) {
            fireTextAddedEvent(new JaxeEditEvent(this, offset, str));
//            textPane.finEditionSpeciale();
        }

    }
    
    /**
     * Mise � jour des indentations apr�s une suppression de \n (appel� par JaxeUndoableEdit)
     */
    protected void majIndentSupp(final int offset) {
        final Properties prefs = Preferences.getPref();
        final boolean consIndent = (prefs != null && "true".equals(prefs.getProperty("consIndent")));
        if (consIndent)
            return;
        JaxeElement je = elementA(offset);
        if (je != null) {
            if (je instanceof JETexte)
                je = je.getParent();
            if (je.avecIndentation()) {
                if (je.fin.getOffset() == offset) {
                    final Style s = textPane.addStyle(null, null);
                    StyleConstants.setLeftIndent(s, (float)20.0*je.indentations());
                    setParagraphAttributes(offset, 1, s, false);
                    return;
                }
            }
        }
    }
    
    /**
     * Mise � jour des indentations apr�s un ajout de \n (appel� par JaxeUndoableEdit)
     */
    protected void majIndentAjout(final int offset) {
        final Properties prefs = Preferences.getPref();
        final boolean consIndent = (prefs != null && "true".equals(prefs.getProperty("consIndent")));
        if (!consIndent) {
            JaxeElement je = elementA(offset-1);
            while (je instanceof JETexte || je instanceof JEStyle)
                je = je.getParent();
            if (je == null)
                return;
            if (!je.avecIndentation() && je.fin.getOffset() == offset - 1)
                je = je.getParent();
            if (je.avecIndentation()) {
                textPane.debutIgnorerEdition();
                if (je.debut.getOffset() <= offset-1 && je.fin.getOffset() > offset+1) {
                    final Style s = textPane.addStyle(null, null);
                    StyleConstants.setLeftIndent(s, (float)20.0*(je.indentations()+1));
                    setParagraphAttributes(offset+1, 1, s, false);
                } else if (je.fin.getOffset()-1 == offset &&
                        getParagraphElement(offset).getStartOffset() > je.debut.getOffset()) {
                    final Style s = textPane.addStyle(null, null);
                    StyleConstants.setLeftIndent(s, (float)20.0*(je.indentations()+1));
                    setParagraphAttributes(offset, 1, s, false);
                } else if (je.fin.getOffset() == offset-1 && je.getParent() != null &&
                        je.getParent().debut.getOffset() <
                        getParagraphElement(offset).getStartOffset()) {
                    final Style s = textPane.addStyle(null, null);
                    StyleConstants.setLeftIndent(s, (float)20.0*je.indentations());
                    setParagraphAttributes(offset-1, 1, s, false);
                }
                textPane.finIgnorerEdition();
            }
            je = elementA(offset+1);
            while (je instanceof JETexte || je instanceof JEStyle)
                je = je.getParent();
            if (je == null)
                return;
            if (je.avecIndentation() && je.fin.getOffset() == offset+1) {
                textPane.debutIgnorerEdition();
                final Style s = textPane.addStyle(null, null);
                StyleConstants.setLeftIndent(s, (float)20.0*je.indentations());
                setParagraphAttributes(offset+1, 1, s, false);
                textPane.finIgnorerEdition();
            }
        }
    }

    /* ne marche pas :(
    public void myInsertStuff(javax.swing.text.AbstractDocument.DefaultDocumentEvent chng,
            AttributeSet attr, int off, String str) {
        writeLock();
        try {
            try {
                UndoableEdit u = getContent().insertString(off, str);
                DefaultDocumentEvent e = 
                    new DefaultDocumentEvent(off, str.length(), DocumentEvent.EventType.INSERT);
                if (u != null) {
                    chng.addEdit(u);
                }
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            //buffer.insert(off, str.length(), data, chng);
            super.insertUpdate(chng, attr);
            chng.end();
            fireInsertUpdate(chng);
        fireUndoableEditUpdate(new UndoableEditEvent(this, chng));
        } finally {
            writeUnlock();
        }
    }
    */
    
    public class SwingElementSpec {
        public String balise;
        public boolean branche;
        public String texte;
        int offset;
        public ArrayList<SwingElementSpec> enfants;
        SimpleAttributeSet att;

        public SwingElementSpec(final String balise) {
            this.balise = balise;
            branche = true;
            texte = null;
            enfants = new ArrayList<SwingElementSpec>();
            att = null;
        }

        public SwingElementSpec(final String balise, final SimpleAttributeSet att) {
            this.balise = balise;
            branche = true;
            texte = null;
            enfants = new ArrayList<SwingElementSpec>();
            this.att = att;
        }

        public SwingElementSpec(final String balise, final int offset, final String texte) {
            this.balise = balise;
            branche = false;
            this.offset = offset;
            this.texte = texte;
            enfants = null;
            att = null;
        }

        public void ajEnfant(final SwingElementSpec enfant) {
            enfants.add(enfant);
        }

        public ArrayList<ElementSpec> getElementSpecs() {
            final ArrayList<ElementSpec> specs = new ArrayList<ElementSpec>();
            if (!branche) {
                final SimpleAttributeSet attcontent = new SimpleAttributeSet();
                attcontent.addAttribute(AbstractDocument.ElementNameAttribute, "content");
                if (texte == null)
                    specs.add(new ElementSpec(attcontent,
                            ElementSpec.ContentType));
                else
                    specs.add(new ElementSpec(attcontent,
                            ElementSpec.ContentType, texte.toCharArray(),
                        offset, texte.length()));
            } else {
                SimpleAttributeSet att2 = new SimpleAttributeSet();
                if (att != null)
                    att2.addAttributes(att);
                att2.addAttribute(AbstractDocument.ElementNameAttribute, balise);
                specs.add(new ElementSpec(att2, ElementSpec.StartTagType));
                for (final SwingElementSpec enfant : enfants) {
                    specs.addAll(enfant.getElementSpecs());
                }
                if (att != null) {
                    att2 = new SimpleAttributeSet();
                    att2.addAttribute(AbstractDocument.ElementNameAttribute, balise);
                }
                specs.add(new ElementSpec(att2, ElementSpec.EndTagType));
            }
            return (specs);
        }

        public String getTexteArbre() {
            if (branche) {
                String atexte = "";
                for (final SwingElementSpec enfant : enfants) {
                    final String etexte = enfant.getTexteArbre();
                    if (etexte != null) atexte += etexte;
                }
                return (atexte);
            }
            return (texte);
        }
    }

    public SwingElementSpec prepareSpec(final String baliseSpec) {
        return (new SwingElementSpec(baliseSpec));
    }

    public SwingElementSpec prepareSpec(final String baliseSpec,
            final SimpleAttributeSet att) {
        return (new SwingElementSpec(baliseSpec, att));
    }

    public SwingElementSpec prepareSpec(final String baliseSpec, final int offset,
            final String texte) {
        return (new SwingElementSpec(baliseSpec, offset, texte));
    }

    public void sousSpec(final SwingElementSpec parentspec,
            final SwingElementSpec enfantspec) {
        parentspec.ajEnfant(enfantspec);
    }

    public javax.swing.text.Element insereSpec(final SwingElementSpec jspec,
            final int offset) {
        final ArrayList<ElementSpec> vspecs = jspec.getElementSpecs();
        ElementSpec[] es = new ElementSpec[vspecs.size()];
        es = vspecs.toArray(es);
        final String texte = jspec.getTexteArbre();

        writeLock();
        try {
            DefaultDocumentEvent evnt = null;
            try {
                final UndoableEdit cEdit = getContent().insertString(offset, texte);
                evnt = new DefaultDocumentEvent(offset, texte.length(),
                        DocumentEvent.EventType.INSERT);
                evnt.addEdit(cEdit);
            } catch (final BadLocationException ex) {
                LOG.error("insereSpec(SwingElementSpec, int)", ex);
            }
            buffer.insert(offset, texte.length(), es, evnt);
            // update bidi (possibly)
            //AbstractDocument.super.insertUpdate(evnt, null);
            // notify the listeners
            evnt.end();
            fireInsertUpdate(evnt);
            fireUndoableEditUpdate(new UndoableEditEvent(this, evnt));
        } finally {
            writeUnlock();
        }
        return (elementTexteA(jspec.balise, offset));
    }

    public javax.swing.text.Element elementTexteA(final String nom, final int offset) {
        BranchElement branche = (BranchElement) getDefaultRootElement();
        while (branche != null && branche.getStartOffset() != offset) {
            final javax.swing.text.Element el = branche.positionToElement(offset);
            if (el instanceof BranchElement)
                branche = (BranchElement) el;
            else
                branche = null;
        }
        return (branche);
    }

    public EditorKit createEditorKit() {
        return (new JaxeEditorKit());
    }

    class JaxeEditorKit extends StyledEditorKit {

        protected ViewFactory myViewFactory;

        public JaxeEditorKit() {
            super();
            myViewFactory = new JaxeViewFactory();
        }

        @Override
        public ViewFactory getViewFactory() {
            return (myViewFactory);
        }
    }

    class JaxeViewFactory implements ViewFactory {

        public View create(final javax.swing.text.Element elem) {
            final String kind = elem.getName();
            if (kind != null) {
                if (kind.equals(AbstractDocument.ContentElementName)) {
                    return new LabelView(elem);
                } else if (kind.equals(AbstractDocument.ParagraphElementName)) {
                    //return new JaxeSpecialParagraph(elem);
                    return new ParagraphView(elem);
                } else if (kind.equals(AbstractDocument.SectionElementName)) {
                    return new BoxView(elem, View.Y_AXIS);
                } else if (kind.equals(StyleConstants.ComponentElementName)) {
                    return new ComponentView(elem);
                } else if (kind.equals(StyleConstants.IconElementName)) {
                    return new IconView(elem);
                } else if (kind.equals("table")) { return new JaxeTableView(
                        elem); }
            }

            // default to text display
            return new LabelView(elem);
        }
    }
    
    /*
    // pour �viter View.preferenceChanged qui finit par appeler revalidate de tout le textpane !
    class JaxeSpecialParagraph extends ParagraphView {
        boolean sansparent = false;
        public JaxeSpecialParagraph(javax.swing.text.Element elem) {
            super(elem);
        }
        public void preferenceChanged(View child, boolean width, boolean height) {
            // impossible d'utiliser les attributs prot�g�s majorReqValid, majorAllocValid, minorReqValid, minorAllocValid
            // -> on change le parent temporairement pour �viter la propagation vers les parents
            if (!height)
                sansparent = true;
            super.preferenceChanged(child, width, height);
            if (!height)
                sansparent = false;
            // il faut maintenant repositionner les composants du paragraphe, cf BasicTextUI.layoutContainer (qui est appel� apr�s le revalidate pour tous les composants)
            if (!height && textPane != null) {
                // � faire: acquisition du lock ?
                final java.awt.Rectangle alloc = getVisibleEditorRect();
                for (int i=0; i<getViewCount(); i++) {
                    View vi = getView(i);
                    for (int j=0; j<vi.getViewCount(); j++) {
                        if (vi.getView(j) instanceof javax.swing.text.ComponentView) {
                            final ComponentView cv = (javax.swing.text.ComponentView)vi.getView(j);
                            // il faut utiliser l'Invalidator � la place de cv.getComponent(), mais cv.c est priv� -> on le r�cup�re avec getParent()
                            final java.awt.Component c = cv.getComponent().getParent();
                            System.out.println("component " + c);
                            final java.awt.Shape ca = calculateViewPosition(alloc, cv);
                            if (ca != null) {
                                final java.awt.Rectangle compAlloc = (ca instanceof java.awt.Rectangle) ? (java.awt.Rectangle) ca : ca.getBounds();
                                System.out.println("setBounds " + compAlloc); // pb: le rectangle ne change pas
                                c.setBounds(compAlloc); // pb: cela appelle ComponentView$Invalidator.invalidate ? qui appelle preferenceChanged
                                // �a ne marche pas (le composant n'est pas d�plac�)
                            }
                        }
                    }
                }
            }
        }
        java.awt.Rectangle getVisibleEditorRect() {
            final java.awt.Rectangle alloc = textPane.getBounds();
            if ((alloc.width > 0) && (alloc.height > 0)) {
                alloc.x = alloc.y = 0;
                final java.awt.Insets insets = textPane.getInsets();
                alloc.x += insets.left;
                alloc.y += insets.top;
                alloc.width -= insets.left + insets.right;
                alloc.height -= insets.top + insets.bottom;
                return(alloc);
            }
            return(null);
        }
        java.awt.Shape calculateViewPosition(java.awt.Shape alloc, View v) {
            int pos = v.getStartOffset();
            View child = null;
            for (View parent = textPane.getUI().getRootView(textPane); (parent != null) && (parent != v); parent = child) {
                int index = parent.getViewIndex(pos, Position.Bias.Forward);
                alloc = parent.getChildAllocation(index, alloc);
                child = parent.getView(index);
            }
            return (child != null) ? alloc : null;
        }
        public View getParent() {
            if (sansparent)
                return(null);
            return(super.getParent());
        }
    }
    */
    
    /*class JaxeSpecialParagraph extends ParagraphView {
        
        public JaxeSpecialParagraph(javax.swing.text.Element elem) {
            super(elem);
            setInsets((short)3, (short)3, (short)3, (short)3);
        }
        
        public void paint(Graphics g, Shape allocation) {
            super.paint(g, allocation);
            Rectangle alloc = (allocation instanceof Rectangle) ?
                       (Rectangle)allocation : allocation.getBounds();
            g.setColor(Color.red);
            g.drawRect(alloc.x, alloc.y, alloc.width-1, alloc.height-1);
            g.setColor(Color.black);
        }
    }*/
    
    /*class MyUndoableEditListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            if (e.getEdit() instanceof JaxeUndoableEdit) {
                ((JaxeUndoableEdit)(e.getEdit())).doit();
            }
        }
    }*/
    
    public void styleChanged() { // another bug fix (see Jaxe)
        styleChanged(null);
    }
    
    /*public void imageChanged(int offset) { // another UGLY (Windows/Linux) bug workaround
        // to force a ParagraphView update
        // problem 1: causes a ArrayIndexOutOfBoundsException with Sun's JVM on Linux
        // problem 2: moves the view to wherever the caret is
        textPane.debutIgnorerEdition();
        try {
            super.insertString(offset, "\n", null);
            super.remove(offset, 1);
        } catch (BadLocationException ex) {
            System.err.println("BadLocationException");
        }
        textPane.finIgnorerEdition();
    }*/
    
    public void imageChanged(final JComponent comp) { // yet another UGLY bug workaround
        final Container cont = comp.getParent();
        if (cont == null)
            return;
        if (cont.getLayout() == null)
            cont.setLayout(new OverlayLayout(cont));
        cont.validate();
    }

    /**
     * Returns the JaxeElement that represents the Node
     * @param node get the JaxeElement for this Node
     * @return The representation for the given Node
     */
    public JaxeElement getElementForNode(final Node node) {
        if (node == null)
            return null;
        return dom2JaxeElement.get(node);
    }
    
    /**
     * Adds a listener for editevents
     * @param edit Listener to add
     */
    public void addEditListener(final JaxeEditListenerIf edit) {
        _editListener.add(edit);
    }
    
    /**
     * Removes a listener for editevents
     * @param edit Listener to remove
     */
    public void removeEditListener(final JaxeEditListenerIf edit) {
        _editListener.remove(edit);
    }
    
    /**
     * Fires an event for removing text to all listeners
     * @param event Event to send
     */
    public void fireTextRemovedEvent(final JaxeEditEvent event) {
        for (final JaxeEditListenerIf l : _editListener) {
            l.textRemoved(event);
        }
    }
    
    /**
     * Fires an event for removing JaxeElements to all listeners
     * @param event Event to send
     */
    public void fireElementRemovedEvent(final JaxeEditEvent event) {
        for (final JaxeEditListenerIf l : _editListener) {
            l.elementRemoved(event);
        }
    }
    
    /**
     * Fires an event for adding text to all listeners
     * @param event Event to send
     */
    public void fireTextAddedEvent(final JaxeEditEvent event) {
        for (final JaxeEditListenerIf l : _editListener) {
            l.textAdded(event);
        }
    }
    
    /**
     * Fires an event for adding JaxeElements to all listeners an returns a possible new insert position
     * @param event Event to send
     * @param pos Position element will be added
     * @return New position of insert
     */
    public Position fireElementAddedEvent(final JaxeEditEvent event, Position pos) {
        for (final JaxeEditListenerIf l : _editListener) {
            pos = l.elementAdded(event, pos);
        }
        return pos;
    }

    /**
     * Fires an event to prepare the position a JaxeElement will be added and returns a possible
     * new instert position
     * @param pos Position to prepare
     * @return New position of insert
     */
    public Position firePrepareElementAddEvent(Position pos) {
        for (final JaxeEditListenerIf l : _editListener) {
            pos = l.prepareAddedElement(pos);
        }
        return pos;
    }
}
