/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package io.github.itzispyder.improperui.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.itzispyder.improperui.render.ImproperUIPanel;
import io.github.itzispyder.improperui.render.constants.Alignment;
import io.github.itzispyder.improperui.render.constants.Axis;
import io.github.itzispyder.improperui.render.constants.BackgroundClip;
import io.github.itzispyder.improperui.render.constants.ChildrenAlignment;
import io.github.itzispyder.improperui.render.constants.InputType;
import io.github.itzispyder.improperui.render.constants.Position;
import io.github.itzispyder.improperui.render.constants.Visibility;
import io.github.itzispyder.improperui.render.elements.Positionable;
import io.github.itzispyder.improperui.render.math.Color;
import io.github.itzispyder.improperui.render.math.Dimensions;
import io.github.itzispyder.improperui.script.ScriptArgs;
import io.github.itzispyder.improperui.script.ScriptReader;
import io.github.itzispyder.improperui.script.events.KeyEvent;
import io.github.itzispyder.improperui.script.events.MouseEvent;
import io.github.itzispyder.improperui.util.MathUtils;
import io.github.itzispyder.improperui.util.StringUtils;
import io.github.itzispyder.improperui.util.render.RenderUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Element {
    public static final Comparator<Object> ORDER = Comparator.comparing(e -> ((Element)e).order).reversed();
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    private static int sequence = 0;
    private String id;
    private String tag;
    public final Set<String> classList;
    public int order = 0;
    public Position position;
    public int marginLeft;
    public int marginRight;
    public int marginTop;
    public int marginBottom;
    public int paddingLeft;
    public int paddingRight;
    public int paddingTop;
    public int paddingBottom;
    public int x;
    public int y;
    public int width;
    public int height;
    public Color borderColor;
    public Color fillColor;
    public Color shadowColor;
    public Color shadowFadeColor;
    public Color textColor;
    public int borderThickness;
    public int borderRadius;
    public int shadowDistance;
    public String clickAction;
    public String scrollAction;
    public String keyAction;
    public Alignment textAlignment;
    public String innerText;
    public String innerTextPrefix;
    public String innerTextSuffix;
    public float textScale;
    public boolean textShadow;
    public ChildrenAlignment childrenAlignment;
    public int gridColumns;
    public Visibility visibility;
    public BackgroundClip backgroundClip;
    public BackgroundClip childrenConstraint;
    public Identifier backgroundImage;
    public float opacity;
    public boolean draggable;
    public boolean scrollable;
    public boolean clickThrough;
    public int rotateX;
    public int rotateY;
    public int rotateZ;
    public Element hoverStyle;
    public Element focusStyle;
    public Element selectStyle;
    public Element parent;
    public ImproperUIPanel parentPanel;
    private final List<Element> children;
    private final Map<String, Consumer<ScriptArgs>> properties;
    private final List<String> queuedProperties = new ArrayList<String>();

    public Element() {
        this(0, 0, 0, 0);
    }

    public Element(int x, int y, int width, int height) {
        this.children = new ArrayList<Element>();
        this.properties = new HashMap<String, Consumer<ScriptArgs>>();
        this.classList = new HashSet<String>();
        this.position = Position.INHERIT;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.marginBottom = 0;
        this.marginTop = 0;
        this.marginRight = 0;
        this.marginLeft = 0;
        this.paddingBottom = 0;
        this.paddingTop = 0;
        this.paddingRight = 0;
        this.paddingLeft = 0;
        this.shadowColor = new Color(Integer.MIN_VALUE);
        this.shadowFadeColor = this.shadowColor.withAlpha(0);
        this.fillColor = new Color(-16777216);
        this.borderColor = new Color(-14671840);
        this.textColor = new Color(-3092272);
        this.shadowDistance = 0;
        this.borderThickness = 0;
        this.borderRadius = 0;
        this.textScale = 1.0f;
        this.textAlignment = Alignment.LEFT;
        this.textShadow = false;
        this.childrenAlignment = ChildrenAlignment.NONE;
        this.childrenConstraint = BackgroundClip.NONE;
        this.gridColumns = 1;
        this.visibility = Visibility.VISIBLE;
        this.backgroundClip = BackgroundClip.NONE;
        this.opacity = 1.0f;
        this.clickThrough = false;
        this.scrollable = false;
        this.draggable = false;
        this.rotateZ = 0;
        this.rotateY = 0;
        this.rotateX = 0;
        this.init();
    }

    public void init() {
        this.registerProperty("position", args -> {
            this.position = args.get(0).toEnum(Position.class);
        });
        this.registerProperty("x", args -> {
            this.x = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("y", args -> {
            this.y = this.parseIntValue(args.get(0), true);
        });
        this.registerProperty("width", args -> {
            this.width = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("height", args -> {
            this.height = this.parseIntValue(args.get(0), true);
        });
        this.registerProperty("pos", args -> this.position(this.parseIntValue(args.get(0), false), this.parseIntValue(args.get(1), true)));
        this.registerProperty("size", args -> this.size(this.parseIntValue(args.get(0), false), this.parseIntValue(args.get(1), true)));
        this.registerProperty("center", args -> {
            switch (args.get(0).toEnum(Axis.class)) {
                case HORIZONTAL: {
                    int len = this.parent == null ? RenderUtils.width() : this.parent.width;
                    this.x = (len - this.width) / 2;
                    break;
                }
                case VERTICAL: {
                    int len = this.parent == null ? RenderUtils.height() : this.parent.height;
                    this.y = (len - this.height) / 2;
                    break;
                }
                case BOTH: {
                    int lenWidth = this.parent == null ? RenderUtils.width() : this.parent.width;
                    int lenHeight = this.parent == null ? RenderUtils.height() : this.parent.height;
                    this.x = (lenWidth - this.width) / 2;
                    this.y = (lenHeight - this.height) / 2;
                }
            }
        });
        this.registerProperty("padding-left", args -> {
            this.paddingLeft = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("padding-right", args -> {
            this.paddingRight = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("padding-top", args -> {
            this.paddingTop = this.parseIntValue(args.get(0), true);
        });
        this.registerProperty("padding-bottom", args -> {
            this.paddingBottom = this.parseIntValue(args.get(0), true);
        });
        this.registerProperty("padding", args -> this.padding(this.parseIntValue(args.get(0), false)));
        this.registerProperty("margin-left", args -> {
            this.marginLeft = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("margin-right", args -> {
            this.marginRight = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("margin-top", args -> {
            this.marginTop = this.parseIntValue(args.get(0), true);
        });
        this.registerProperty("margin-bottom", args -> {
            this.marginBottom = this.parseIntValue(args.get(0), true);
        });
        this.registerProperty("margin", args -> this.margin(this.parseIntValue(args.get(0), false)));
        this.registerProperty("border-thickness", args -> {
            this.borderThickness = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("border-radius", args -> {
            this.borderRadius = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("border-color", args -> {
            this.borderColor = args.get(0).toColor();
        });
        this.registerProperty("border", args -> this.border(this.parseIntValue(args.get(0), false), this.parseIntValue(args.get(1), false), args.get(2).toColor()));
        this.registerProperty("fill-color", args -> {
            this.fillColor = args.get(0).toColor();
        });
        this.registerProperty("shadow-color", args -> {
            this.shadowColor = args.get(0).toColor();
            this.shadowFadeColor = this.shadowColor.withAlpha(0);
        });
        this.registerProperty("shadow-fade", args -> {
            this.shadowFadeColor = args.get(0).toColor();
        });
        this.registerProperty("shadow-fade-color", args -> {
            this.shadowFadeColor = args.get(0).toColor();
        });
        this.registerProperty("shadow-color-fade", args -> {
            this.shadowFadeColor = args.get(0).toColor();
        });
        this.registerProperty("shadow-distance", args -> {
            this.shadowDistance = this.parseIntValue(args.get(0), false);
        });
        this.registerProperty("shadow", args -> this.shadow(args.get(0).toInt(), args.get(1).toColor()));
        this.registerProperty("scroll-action", args -> {
            this.scrollAction = args.get(0).toString();
        });
        this.registerProperty("click-action", args -> {
            this.clickAction = args.get(0).toString();
        });
        this.registerProperty("key-action", args -> {
            this.keyAction = args.get(0).toString();
        });
        this.registerProperty("on-scroll", args -> {
            this.scrollAction = args.get(0).toString();
        });
        this.registerProperty("on-click", args -> {
            this.clickAction = args.get(0).toString();
        });
        this.registerProperty("on-key", args -> {
            this.keyAction = args.get(0).toString();
        });
        this.registerProperty("inner-text", args -> {
            this.innerText = StringUtils.color(args.getQuoteAndRemove());
        });
        this.registerProperty("inner-text-prefix", args -> {
            this.innerTextPrefix = StringUtils.color(args.getQuoteAndRemove());
        });
        this.registerProperty("inner-text-suffix", args -> {
            this.innerTextSuffix = StringUtils.color(args.getQuoteAndRemove());
        });
        this.registerProperty("text-scale", args -> {
            this.textScale = args.get(0).toFloat();
        });
        this.registerProperty("text-shadow", args -> {
            this.textShadow = args.get(0).toBool();
        });
        this.registerProperty("text-align", args -> {
            this.textAlignment = args.get(0).toEnum(Alignment.class);
        });
        this.registerProperty("text-color", args -> {
            this.textColor = args.get(0).toColor();
        });
        this.registerProperty("color", args -> {
            this.textColor = args.get(0).toColor();
        });
        this.registerProperty("children-align", args -> {
            this.childrenAlignment = args.get(0).toEnum(ChildrenAlignment.class);
        });
        this.registerProperty("child-align", args -> {
            this.childrenAlignment = args.get(0).toEnum(ChildrenAlignment.class);
        });
        this.registerProperty("children-constraint", args -> {
            this.childrenConstraint = args.get(0).toEnum(BackgroundClip.class);
        });
        this.registerProperty("children-bounds", args -> {
            this.childrenConstraint = args.get(0).toEnum(BackgroundClip.class);
        });
        this.registerProperty("children-bound", args -> {
            this.childrenConstraint = args.get(0).toEnum(BackgroundClip.class);
        });
        this.registerProperty("child-constraint", args -> {
            this.childrenConstraint = args.get(0).toEnum(BackgroundClip.class);
        });
        this.registerProperty("child-bounds", args -> {
            this.childrenConstraint = args.get(0).toEnum(BackgroundClip.class);
        });
        this.registerProperty("child-bound", args -> {
            this.childrenConstraint = args.get(0).toEnum(BackgroundClip.class);
        });
        this.registerProperty("display", args -> {
            this.childrenAlignment = args.get(0).toEnum(ChildrenAlignment.class);
        });
        this.registerProperty("grid-columns", args -> {
            this.gridColumns = args.get(0).toInt();
        });
        this.registerProperty("visibility", args -> {
            this.visibility = args.get(0).toEnum(Visibility.class);
        });
        this.registerProperty("background-clip", args -> {
            this.backgroundClip = args.get(0).toEnum(BackgroundClip.class);
        });
        this.registerProperty("background-color", args -> {
            this.fillColor = args.get(0).toColor();
        });
        this.registerProperty("background-image", args -> {
            this.backgroundImage = Identifier.of((String)args.get(0).toString());
        });
        this.registerProperty("opacity", args -> {
            this.opacity = args.get(0).toFloat();
        });
        this.registerProperty("draggable", args -> {
            this.draggable = args.get(0).toBool();
        });
        this.registerProperty("scrollable", args -> {
            this.scrollable = args.get(0).toBool();
        });
        this.registerProperty("click-through", args -> {
            this.clickThrough = args.get(0).toBool();
        });
        this.registerProperty("rotate-x", args -> {
            this.rotateX = args.get(0).toInt();
        });
        this.registerProperty("rotate-y", args -> {
            this.rotateY = args.get(0).toInt();
        });
        this.registerProperty("rotate-z", args -> {
            this.rotateZ = args.get(0).toInt();
        });
        this.registerProperty("rotate", args -> this.rotate(args.get(0).toInt(), args.get(1).toInt(), args.get(2).toInt()));
        this.registerProperty("hover", args -> {
            this.hoverStyle = this.parsePropertiesThenSet(this.hoverStyle, args.getAll().toString());
        });
        this.registerProperty("select", args -> {
            this.selectStyle = this.parsePropertiesThenSet(this.selectStyle, args.getAll().toString());
        });
        this.registerProperty("focus", args -> {
            this.focusStyle = this.parsePropertiesThenSet(this.focusStyle, args.getAll().toString());
        });
        this.registerProperty("hovered", args -> {
            this.hoverStyle = this.parsePropertiesThenSet(this.hoverStyle, args.getAll().toString());
        });
        this.registerProperty("selected", args -> {
            this.selectStyle = this.parsePropertiesThenSet(this.selectStyle, args.getAll().toString());
        });
        this.registerProperty("focused", args -> {
            this.focusStyle = this.parsePropertiesThenSet(this.focusStyle, args.getAll().toString());
        });
        this.registerProperty("order", args -> {
            this.order = args.get(0).toInt();
        });
        this.registerProperty("z-index", args -> {
            this.order = args.get(0).toInt();
        });
    }

    private int parseIntValue(ScriptArgs.Arg arg, boolean forHeight) {
        String str = arg.toString();
        if (str.matches("\\d+$")) {
            return arg.toInt();
        }
        if (str.endsWith("%")) {
            str = str.substring(0, str.length() - 1);
            double percent = (double)Integer.parseInt(str) / 100.0;
            int len = forHeight ? (this.parent == null ? RenderUtils.height() : this.parent.height) : (this.parent == null ? RenderUtils.width() : this.parent.width);
            return (int)((double)len * percent);
        }
        int val = Integer.parseInt(str.substring(0, str.length() - 2));
        double percent = (double)val / 100.0;
        if (str.endsWith("vw")) {
            return (int)((double)RenderUtils.width() * percent);
        }
        if (str.endsWith("vh")) {
            return (int)((double)RenderUtils.height() * percent);
        }
        if (str.endsWith("px")) {
            return val;
        }
        return arg.toInt();
    }

    public Element margin(int margin) {
        this.marginTop = this.marginBottom = margin;
        this.marginRight = this.marginBottom;
        this.marginLeft = this.marginBottom;
        return this;
    }

    public Element padding(int padding) {
        this.paddingTop = this.paddingBottom = padding;
        this.paddingRight = this.paddingBottom;
        this.paddingLeft = this.paddingBottom;
        return this;
    }

    public Element border(int borderThickness, int borderRadius, Color borderColor) {
        this.borderThickness = borderThickness;
        this.borderRadius = borderRadius;
        this.borderColor = borderColor;
        return this;
    }

    public Element shadow(int shadowDistance, Color shadowColor) {
        this.shadowDistance = shadowDistance;
        this.shadowColor = shadowColor;
        this.shadowFadeColor = shadowColor.withAlpha(0);
        return this;
    }

    public Element size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Element position(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Element rotate(int x, int y, int z) {
        this.rotateX = x;
        this.rotateY = y;
        this.rotateZ = z;
        return this;
    }

    public int getPosX() {
        return this.position == Position.INHERIT && this.parent != null ? this.parent.getPosX() + this.parent.marginLeft + this.x : this.x;
    }

    public int getPosY() {
        return this.position == Position.INHERIT && this.parent != null ? this.parent.getPosY() + this.parent.marginTop + this.y : this.y;
    }

    public Dimensions getRawDimensions() {
        return new Dimensions(this.x, this.y, this.width, this.height);
    }

    public Dimensions getDimensions() {
        return new Dimensions(this.getPosX(), this.getPosY(), this.width, this.height);
    }

    public Dimensions getPaddedDimensions() {
        Dimensions dim = this.getDimensions();
        return dim.withX(dim.x - this.paddingLeft).withY(dim.y - this.paddingTop).withWidth(dim.width + this.paddingLeft + this.paddingRight).withHeight(dim.height + this.paddingTop + this.paddingBottom);
    }

    public Dimensions getBorderedDimensions() {
        Dimensions dim = this.getPaddedDimensions();
        return dim.withX(dim.x - this.borderThickness).withY(dim.y - this.borderThickness).withWidth(dim.width + this.borderThickness * 2).withHeight(dim.height + this.borderThickness * 2);
    }

    public Dimensions getMarginalDimensions() {
        Dimensions dim = this.getBorderedDimensions();
        return dim.withX(dim.x - this.marginLeft).withY(dim.y - this.marginTop).withWidth(dim.width + this.marginLeft + this.marginRight).withHeight(dim.height + this.marginTop + this.marginBottom);
    }

    public Dimensions getHitboxDimensions() {
        return new Dimensions(this.getPosX() + this.marginLeft - this.paddingLeft, this.getPosY() + this.marginTop - this.paddingTop, this.width + this.paddingLeft + this.paddingRight, this.height + this.paddingTop + this.paddingBottom);
    }

    public void move(int deltaX, int deltaY) {
        this.x += deltaX;
        this.y += deltaY;
        this.children.stream().filter(child -> child.position == Position.ABSOLUTE).forEach(child -> child.move(deltaX, deltaY));
    }

    public void moveTo(int x, int y) {
        int delX = x - this.x;
        int delY = y - this.y;
        this.x = x;
        this.y = y;
        this.children.stream().filter(child -> child.position == Position.ABSOLUTE).forEach(child -> child.move(delX, delY));
    }

    public void boundIn(Dimensions dim) {
        Dimensions self = this.getMarginalDimensions();
        int x = MathUtils.clamp(self.x, dim.x, dim.widthX - self.width);
        int y = MathUtils.clamp(self.y, dim.y, dim.heightY - self.height);
        this.move(x -= self.x, y -= self.y);
    }

    public void boundInConstraints() {
        Dimensions constraint = this.getConstraint();
        if (constraint != null) {
            this.boundIn(constraint);
        }
    }

    public Dimensions getConstraint() {
        if (this.parent == null) {
            return null;
        }
        return switch (this.parent.childrenConstraint) {
            case BackgroundClip.PADDING -> this.parent.getPaddedDimensions();
            case BackgroundClip.SELF -> this.parent.getDimensions();
            case BackgroundClip.BORDER -> this.parent.getBorderedDimensions();
            case BackgroundClip.MARGIN -> this.parent.getMarginalDimensions();
            default -> null;
        };
    }

    public void addChild(Element child) {
        if (child == null || child == this || child.parent != null || this.children.contains(child)) {
            return;
        }
        this.children.add(child);
        child.parent = this;
    }

    public void removeChild(Element child) {
        if (child == null) {
            return;
        }
        child.parent = null;
        this.children.remove(child);
    }

    public List<Element> getChildren() {
        return new ArrayList<Element>(this.children);
    }

    public List<Element> getChildrenOrdered() {
        return new ArrayList<Object>(this.getChildren().stream().sorted(ORDER).toList());
    }

    public void clearChildren() {
        List<Element> children = this.getChildren();
        children.forEach(this::removeChild);
    }

    public void registerProperty(String key, Consumer<ScriptArgs> callback) {
        if (key != null && !key.isEmpty() && callback != null) {
            this.properties.put(key.toLowerCase(), callback);
        }
    }

    public String getId() {
        return this.id;
    }

    public String getTag() {
        return this.tag;
    }

    public void callProperty(String entry) {
        String regex = "\\s*((=>)|(->)|:|=)\\s*";
        String[] split = entry.trim().split(regex);
        if (split.length < 2 || !this.properties.containsKey(split[0])) {
            return;
        }
        String value = entry.substring(split[0].length()).replaceFirst(regex, "");
        this.properties.get(split[0]).accept(new ScriptArgs(value.split("\\s+")));
    }

    public void callAttribute(String entry) {
        entry = entry.trim();
        int len = entry.length();
        if (entry.startsWith("#") && len > 1) {
            this.id = entry.substring(1);
        } else if (entry.startsWith("-") && len > 1) {
            this.classList.add(entry.substring(1));
        } else if (len > 0) {
            this.tag = entry;
        }
    }

    public void printAll() {
        System.out.println(this);
        this.children.forEach(Element::printAll);
    }

    public void queueProperty(String entry, boolean highPriority) {
        if (highPriority) {
            this.queuedProperties.add(0, entry);
        } else {
            this.queuedProperties.add(entry);
        }
    }

    public void queueProperty(String entry) {
        this.queueProperty(entry, false);
    }

    public void style() {
        this.order = sequence++;
        this.queuedProperties.forEach(this::callProperty);
        this.children.forEach(Element::style);
        int columnX = 0;
        int rowY = 0;
        int column = 0;
        if (this.childrenAlignment == ChildrenAlignment.GRID) {
            for (Element e : this.children) {
                e.position = Position.INHERIT;
                e.moveTo(columnX, rowY);
                if (e instanceof Positionable) {
                    e.style();
                }
                Dimensions dim = e.getMarginalDimensions();
                columnX += dim.width;
                if (column++ < this.gridColumns - 1) continue;
                column = 0;
                columnX = 0;
                rowY += dim.height;
            }
        }
        this.children.forEach(Element::boundInConstraints);
    }

    public String getText() {
        if (this.innerText == null) {
            return null;
        }
        Object text = this.innerText;
        if (this.innerTextPrefix != null) {
            text = this.innerTextPrefix + (String)text;
        }
        if (this.innerTextSuffix != null) {
            text = (String)text + this.innerTextSuffix;
        }
        return text;
    }

    public void onRender(DrawContext context, int mx, int my, float delta) {
        if (this.parentPanel != null) {
            if (this.parentPanel.selected == this && this.selectStyle != null) {
                this.selectStyle.teleport(this);
                this.selectStyle.onRender(context, mx, my, delta);
                return;
            }
            if (this.parentPanel.hovered == this && this.hoverStyle != null) {
                this.hoverStyle.teleport(this);
                this.hoverStyle.onRender(context, mx, my, delta);
                return;
            }
            if (this.parentPanel.focused == this && this.focusStyle != null) {
                this.focusStyle.teleport(this);
                this.focusStyle.onRender(context, mx, my, delta);
                return;
            }
        }
        int x = this.getPosX();
        int y = this.getPosY();
        if (this.visibility == Visibility.INVISIBLE) {
            return;
        }
        context.getMatrices().pushMatrix();
        int cx = x + this.width / 2;
        int cy = y + this.height / 2;
        context.getMatrices().rotateAbout((float)Math.toRadians(this.rotateZ), (float)cx, (float)cy);
        if (this.visibility != Visibility.ONLY_CHILDREN) {
            String rawText;
            RenderUtils.fillRoundShadow(context, x + this.marginLeft - this.paddingLeft - this.borderThickness, y + this.marginTop - this.paddingTop - this.borderThickness, this.width + this.paddingLeft + this.paddingRight + this.borderThickness * 2, this.height + this.paddingTop + this.paddingBottom + this.borderThickness * 2, this.borderRadius, this.shadowDistance, this.shadowColor.getHexCustomOpacity(this.opacity), this.shadowFadeColor.getHexCustomAlpha(0));
            RenderUtils.fillRoundShadow(context, x + this.marginLeft - this.paddingLeft, y + this.marginTop - this.paddingTop, this.width + this.paddingLeft + this.paddingRight, this.height + this.paddingTop + this.paddingBottom, this.borderRadius, this.borderThickness, this.borderColor.getHexCustomOpacity(this.opacity), this.borderColor.getHexCustomOpacity(this.opacity));
            RenderUtils.fillRoundRect(context, x + this.marginLeft - this.paddingLeft, y + this.marginTop - this.paddingTop, this.width + this.paddingLeft + this.paddingRight, this.height + this.paddingTop + this.paddingBottom, this.borderRadius, this.fillColor.getHexCustomOpacity(this.opacity));
            if (this.backgroundImage != null) {
                RenderUtils.drawRoundTexture(context, this.backgroundImage, x + this.marginLeft - this.paddingLeft, y + this.marginTop - this.paddingTop, this.width + this.paddingLeft + this.paddingRight, this.height + this.paddingTop + this.paddingBottom, this.borderRadius);
            }
            if ((rawText = this.getText()) != null) {
                Text text = Text.of((String)rawText);
                x += this.marginLeft;
                int textY = (int)((float)(y += this.marginTop) + ((float)this.height - this.textScale * 7.0f) / 2.0f);
                switch (this.textAlignment) {
                    case LEFT: {
                        RenderUtils.drawDefaultScaledText(context, text, x, textY, this.textScale, this.textShadow, this.textColor.getHexCustomOpacity(this.opacity));
                        break;
                    }
                    case CENTER: {
                        RenderUtils.drawDefaultCenteredScaledText(context, text, x + this.width / 2, textY, this.textScale, this.textShadow, this.textColor.getHexCustomOpacity(this.opacity));
                        break;
                    }
                    case RIGHT: {
                        RenderUtils.drawDefaultRightScaledText(context, text, x + this.width, textY, this.textScale, this.textShadow, this.textColor.getHexCustomOpacity(this.opacity));
                    }
                }
            }
        }
        if (this.visibility != Visibility.ONLY_SELF) {
            boolean shouldClip;
            boolean bl = shouldClip = this.backgroundClip != BackgroundClip.NONE;
            if (shouldClip) {
                Dimensions shape = switch (this.backgroundClip) {
                    case BackgroundClip.PADDING -> this.getPaddedDimensions();
                    case BackgroundClip.BORDER -> this.getBorderedDimensions();
                    case BackgroundClip.MARGIN -> this.getMarginalDimensions();
                    default -> this.getDimensions();
                };
                context.enableScissor(shape.x, shape.y, shape.x + shape.width, shape.y + shape.height);
            }
            this.onRenderChildren(context, mx, my, delta);
            if (shouldClip) {
                context.disableScissor();
            }
        }
        context.getMatrices().popMatrix();
    }

    public void onRenderChildren(DrawContext context, int mx, int my, float delta) {
        this.getChildren().forEach(child -> child.onRender(context, mx, my, delta));
    }

    public void onLeftClick(int mx, int my, boolean release) {
    }

    public void onRightClick(int mx, int my, boolean release) {
    }

    public void onMiddleClick(int mx, int my, boolean release) {
    }

    public void onScroll(int mx, int my, boolean up) {
    }

    public void onKey(int key, int scan, boolean release) {
    }

    public boolean pollClickable(int button, int mx, int my, boolean release) {
        if (this.clickThrough || this.parentPanel == null || this.visibility == Visibility.INVISIBLE) {
            return false;
        }
        if (!this.getHitboxDimensions().contains(mx, my)) {
            return false;
        }
        if (this.parent != null) {
            if (this.parent.visibility == Visibility.ONLY_SELF) {
                return false;
            }
            if (this.parent.backgroundClip != BackgroundClip.NONE && !this.parent.getHitboxDimensions().contains(mx, my)) {
                return false;
            }
        }
        if (!release) {
            this.parentPanel.selected = this;
            this.parentPanel.focused = this;
            this.parentPanel.cursor[0] = mx;
            this.parentPanel.cursor[1] = my;
        }
        if (this.visibility != Visibility.ONLY_CHILDREN) {
            InputType input = !release ? InputType.CLICK : InputType.RELEASE;
            this.parentPanel.runCallbacks(this.clickAction, new MouseEvent(button, 0, input, this));
        }
        return true;
    }

    public boolean pollScrollable(int mx, int my, boolean up) {
        if (this.clickThrough || this.parentPanel == null || this.visibility == Visibility.INVISIBLE) {
            return false;
        }
        if (!this.getHitboxDimensions().contains(mx, my)) {
            return false;
        }
        if (this.parent != null) {
            if (this.parent.visibility == Visibility.ONLY_SELF) {
                return false;
            }
            if (this.parent.backgroundClip != BackgroundClip.NONE && !this.parent.getHitboxDimensions().contains(mx, my)) {
                return false;
            }
        }
        if (this.visibility != Visibility.ONLY_CHILDREN) {
            int delta = 0;
            if (this.scrollable) {
                for (int i = 0; i < 10; ++i) {
                    Dimensions dim = this.getMarginalDimensions();
                    if (up && this.children.stream().noneMatch(child -> child.getMarginalDimensions().y < dim.y) || !up && this.children.stream().noneMatch(child -> child.getMarginalDimensions().heightY > dim.heightY)) break;
                    int dy = up ? 1 : -1;
                    delta += dy;
                    this.children.forEach(child -> child.move(0, dy));
                }
            }
            this.parentPanel.runCallbacks(this.scrollAction, new MouseEvent(2, delta, InputType.SCROLL, this));
        }
        return true;
    }

    public boolean pollTypeable(int key, int scan, boolean release) {
        if (this.clickThrough || this.parentPanel == null || this.visibility == Visibility.INVISIBLE) {
            return false;
        }
        if (this.parent != null && this.parent.visibility == Visibility.ONLY_SELF) {
            return false;
        }
        if (this.visibility != Visibility.ONLY_CHILDREN) {
            InputType input = !release ? InputType.CLICK : InputType.RELEASE;
            this.parentPanel.runCallbacks(this.keyAction, new KeyEvent(key, scan, input, this));
        }
        return true;
    }

    public void onTick() {
        this.children.forEach(Element::onTick);
    }

    protected void setParentPanel(ImproperUIPanel parentPanel) {
        this.parentPanel = parentPanel;
        this.children.forEach(child -> child.setParentPanel(parentPanel));
    }

    public String toString() {
        JsonObject o = new JsonObject();
        o.addProperty("children-count", (Number)this.children.size());
        o.addProperty("position", this.stringify(this.position));
        JsonArray arr = new JsonArray();
        arr.add((Number)this.x);
        arr.add((Number)this.y);
        arr.add((Number)this.width);
        arr.add((Number)this.height);
        o.add("dimensions", (JsonElement)arr);
        arr = new JsonArray();
        arr.add((Number)this.marginLeft);
        arr.add((Number)this.marginRight);
        arr.add((Number)this.marginTop);
        arr.add((Number)this.marginBottom);
        o.add("margin", (JsonElement)arr);
        arr = new JsonArray();
        arr.add((Number)this.paddingLeft);
        arr.add((Number)this.paddingRight);
        arr.add((Number)this.paddingTop);
        arr.add((Number)this.paddingBottom);
        o.add("padding", (JsonElement)arr);
        arr = new JsonArray();
        arr.add((Number)this.borderThickness);
        arr.add((Number)this.borderRadius);
        arr.add(this.stringify(this.borderColor));
        o.add("border", (JsonElement)arr);
        arr = new JsonArray();
        arr.add(this.stringify(this.fillColor));
        arr.add(this.stringify(this.backgroundClip));
        arr.add(this.stringify(this.backgroundImage));
        arr.add((Number)Float.valueOf(this.opacity));
        o.add("background", (JsonElement)arr);
        arr = new JsonArray();
        arr.add((Number)this.shadowDistance);
        arr.add(this.stringify(this.shadowColor));
        arr.add(this.stringify(this.shadowFadeColor));
        o.add("shadow", (JsonElement)arr);
        arr = new JsonArray();
        arr.add(this.clickAction);
        arr.add(this.scrollAction);
        arr.add(this.keyAction);
        o.add("callbacks", (JsonElement)arr);
        arr = new JsonArray();
        arr.add((Number)Float.valueOf(this.textScale));
        arr.add(this.stringify(this.textAlignment));
        arr.add(this.innerText);
        arr.add(this.innerTextPrefix);
        arr.add(this.innerTextSuffix);
        arr.add(Boolean.valueOf(this.textShadow));
        arr.add(this.stringify(this.textColor));
        o.add("text", (JsonElement)arr);
        arr = new JsonArray();
        arr.add(this.stringify(this.childrenAlignment));
        arr.add(this.stringify(this.childrenConstraint));
        arr.add((Number)this.gridColumns);
        o.add("child-align", (JsonElement)arr);
        arr = new JsonArray();
        arr.add((Number)this.rotateX);
        arr.add((Number)this.rotateY);
        arr.add((Number)this.rotateZ);
        o.add("rotation", (JsonElement)arr);
        o.addProperty("draggable", Boolean.valueOf(this.draggable));
        o.addProperty("scrollable", Boolean.valueOf(this.scrollable));
        o.addProperty("click-through", Boolean.valueOf(this.clickThrough));
        return "%s#%s[%s]%s%s".formatted(this.tag, this.id, this.order, this.classList, o.toString());
    }

    private <T> String stringify(T val, Function<T, String> func) {
        return val == null ? "null" : func.apply(val);
    }

    private <T extends Enum<?>> String stringify(T val) {
        return this.stringify(val, Enum::name);
    }

    private String stringify(Object val) {
        return this.stringify(val, Object::toString);
    }

    public void teleport(Element element) {
        this.moveTo(element.x, element.y);
    }

    public List<Element> collect() {
        ArrayList<Element> list = new ArrayList<Element>();
        for (Element child : this.children) {
            list.add(child);
            list.addAll(child.collect());
        }
        return list;
    }

    public List<Element> collectOrdered() {
        return new ArrayList<Object>(this.collect().stream().sorted(ORDER).toList());
    }

    private Element parsePropertiesThenSet(Element target, String excerpt) {
        if (target == this) {
            return target;
        }
        target = target != null ? target : new Element();
        target.parent = this.parent;
        target.parentPanel = this.parentPanel;
        this.queuedProperties.stream().filter(s -> !s.matches("^(select|hover|focus).*$")).forEach(target::queueProperty);
        ScriptReader.parse(ScriptReader.firstSection(excerpt, '{', '}')).forEach(target::queueProperty);
        target.style();
        return target;
    }
}

