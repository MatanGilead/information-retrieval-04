package App.Model;

public class ParsedDocument {
    private String _doc_id;
    private Integer _class_id;
    private String _title;
    private String _body;

    public ParsedDocument(String doc_id, Integer class_id, String title, String body) {
        this._doc_id = doc_id.trim();
        this._class_id = class_id;
        this._title = title.trim();
        this._body = body.trim().toLowerCase();
        System.out.println(String.format("Added Document: id=%s, class=%s, title =%s", _doc_id, _class_id, _title));
    }

    public String getDocId() {
        return _doc_id;
    }

    public Integer getClassId() {
        return _class_id;
    }

    public String getTitle() {
        return _title;
    }

    public String getBody() {
        return _body;
    }

}