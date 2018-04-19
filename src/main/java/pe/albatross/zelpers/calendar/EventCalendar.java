package pe.albatross.zelpers.calendar;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import pe.albatross.zelpers.miscelanea.JsonHelper;

public class EventCalendar {

    private String id;
    private String title;
    private boolean allDay;
    private String start;
    private String end;
    private String url;
    private String className;
    private boolean editable;
    private boolean startEditable;
    private boolean durationEditable;
    private boolean resourceEditable;
    private String rendering;
    private boolean overlap;
    private String constraint;
    private String source;
    private String color;
    private String backgroundColor;
    private String borderColor;
    private String textColor;

    public EventCalendar() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isStartEditable() {
        return startEditable;
    }

    public void setStartEditable(boolean startEditable) {
        this.startEditable = startEditable;
    }

    public boolean isDurationEditable() {
        return durationEditable;
    }

    public void setDurationEditable(boolean durationEditable) {
        this.durationEditable = durationEditable;
    }

    public boolean isResourceEditable() {
        return resourceEditable;
    }

    public void setResourceEditable(boolean resourceEditable) {
        this.resourceEditable = resourceEditable;
    }

    public String getRendering() {
        return rendering;
    }

    public void setRendering(String rendering) {
        this.rendering = rendering;
    }

    public boolean isOverlap() {
        return overlap;
    }

    public void setOverlap(boolean overlap) {
        this.overlap = overlap;
    }

    public String getConstraint() {
        return constraint;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public ObjectNode toJson() {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode json = JsonHelper.createJson(this, factory, true);
        return json;
    }

}
