package no.capraconsulting.siren.vocabulary;

/**
 * Class provides constants related to the Dublin Core Schema.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Dublin_Core">Dublin Core</a>
 */
public final class DublinCore {
    private DublinCore() {
    }

    /**
     * Lowercase version of "title" for string search simplicity
     */
    public static final String ALTERNATIVE = "alternative";

    /**
     * Denotes first available datetime observation found in the corresponding data set of a part
     */
    public static final String AVAILABLE = "available";

    /**
     * Identifies the user who last modified an entity
     */
    public static final String CONTRIBUTOR = "contributor";

    /**
     * Denotes when an entity was created
     */
    public static final String CREATED = "created";

    /**
     * Identifies the user who created an entity
     */
    public static final String CREATOR = "creator";

    /**
     * Description of an entity
     */
    public static final String DESCRIPTION = "description";

    public static final String EXTENT = "extent";

    /**
     * Denotes the current version of an entity or part
     */
    public static final String HAS_VERSION = "hasVersion";

    /**
     * Unique identifier of an entity, part or version. Required when updating an entity
     */
    public static final String IDENTIFIER = "identifier";

    /**
     * Denotes the parent entity of a part
     */
    public static final String IS_PART_OF = "isPartOf";

    public static final String IS_REFERENCED_BY = "isReferencedBy";

    /**
     * Denotes the parent entity or part of a version
     */
    public static final String IS_VERSION_OF = "isVersionOf";

    /**
     * Denotes when an entity was issued (locked)
     */
    public static final String ISSUED = "issued";

    public static final String MEDIATOR = "mediator";

    /**
     * Denotes when an entity was last modified
     */
    public static final String MODIFIED = "modified";

    /**
     * Identifies the change request command applied to an entity, part and/or version
     */
    public static final String PROVENANCE = "provenance";

    /**
     * Identifies the user who issued (locked) an entity
     */
    public static final String PUBLISHER = "publisher";

    /**
     * Uniquely identifies an entity or version by external relation (third party identifier). Can not be changed on an existing entity
     */
    public static final String REFERENCES = "references";

    public static final String RELATION = "relation";

    /**
     * Identifies the previous version of a version
     */
    public static final String REPLACES = "replaces";

    /**
     * Identifies the source (namespace) of an entity
     */
    public static final String SOURCE = "source";

    /**
     * Unique identifier (name) of the attribute of an entity. Optional subject of an entity
     */
    public static final String SUBJECT = "subject";

    /**
     * Identifies the searchable name of an entity. Required when creating an entity
     */
    public static final String TITLE = "title";

    /**
     * Identifies the type of an entity
     */
    public static final String TYPE = "type";

    /**
     * Denotes when an entity was no longer valid (deleted).
     * Also denotes the last available datetime observation found in the corresponding data set of a part
     */
    public static final String VALID = "valid";
}
