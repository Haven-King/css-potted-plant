package dev.hephaestus.foml.obj;

import de.javagl.obj.FloatTuple;
import de.javagl.obj.Mtl;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjSplitting;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class OBJBuilder {
    public static final SpriteIdentifier DEFAULT_SPRITE = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, null);
    private MeshBuilder meshBuilder;
    private QuadEmitter quadEmitter;
    private final Obj obj;
    private final List<Mtl> mtlList;
    private final SpriteIdentifier sprite;
    private Function<SpriteIdentifier, Sprite> textureGetter;

    public OBJBuilder(Obj obj, List<Mtl> mtlList) {
        this.meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
        this.quadEmitter = this.meshBuilder.getEmitter();
        this.obj = obj;
        this.mtlList = mtlList;

        Mtl mtl = this.findMtlForName("sprite");
        sprite = mtlList.size() > 0
                ? new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier((mtl == null ? mtlList.get(0) : mtl).getMapKd()))
                : DEFAULT_SPRITE;
    }

    private void addVertex(int faceIndex, int vertIndex, FloatTuple vertex, FloatTuple normal, QuadEmitter emitter, Sprite mtlSprite, Obj matGroup, boolean degenerate) {
        int textureCoordIndex = vertIndex;
        if (degenerate) {
            textureCoordIndex = vertIndex - 1;
        }

        this.quadEmitter.pos(vertIndex, vertex.getX(), vertex.getY(), vertex.getZ());
        this.quadEmitter.normal(vertIndex, normal.getX(), normal.getY(), normal.getZ());
        if (this.obj.getNumTexCoords() > 0) {
            FloatTuple text = matGroup.getTexCoord(matGroup.getFace(faceIndex).getTexCoordIndex(textureCoordIndex));
            this.quadEmitter.sprite(vertIndex, 0, text.getX(), 1.0F - text.getY());
        } else {
            this.quadEmitter.nominalFace(Direction.getFacing(normal.getX(), normal.getY(), normal.getZ()));
        }

    }

    public Mesh build() {
        Map<String, Obj> materialGroups = ObjSplitting.splitByMaterialGroups(this.obj);

        for (Entry<String, Obj> entry : materialGroups.entrySet()) {
            String matName = entry.getKey();
            Obj matGroupObj =  entry.getValue();
            Mtl mtl = this.findMtlForName(matName);
            FloatTuple diffuseColor = null;
            FloatTuple specularColor = null;
            Sprite mtlSprite = (Sprite) this.textureGetter.apply(DEFAULT_SPRITE);
            if (mtl != null) {
                diffuseColor = mtl.getKd();
                specularColor = mtl.getKs();
                mtlSprite = this.getMtlSprite(new Identifier(mtl.getMapKd()));
            }

            for (int i = 0; i < matGroupObj.getNumFaces(); ++i) {
                FloatTuple vertex = null;
                FloatTuple normal = null;

                for (int v = 0; v < matGroupObj.getFace(i).getNumVertices(); ++v) {
                    vertex = matGroupObj.getVertex(matGroupObj.getFace(i).getVertexIndex(v));
                    normal = matGroupObj.getNormal(matGroupObj.getFace(i).getNormalIndex(v));
                    this.addVertex(i, v, vertex, normal, this.quadEmitter, mtlSprite, matGroupObj, false);
                }

                if (matGroupObj.getFace(i).getNumVertices() == 3) {
                    this.addVertex(i, 3, vertex, normal, this.quadEmitter, mtlSprite, matGroupObj, true);
                }

                this.quadEmitter.spriteColor(0, -1, -1, -1, -1);
                this.quadEmitter.material(RendererAccess.INSTANCE.getRenderer().materialFinder().find());
                this.quadEmitter.colorIndex(1);
                this.quadEmitter.spriteBake(0, mtlSprite, 32);
                this.quadEmitter.emit();
            }
        }

        return this.meshBuilder.build();
    }

    public List<Mtl> getMtlList() {
        return this.mtlList;
    }

    public Mtl findMtlForName(String name) {
        for (Mtl mtl : mtlList) {
            if(mtl.getName().equals(name)) {
                return mtl;
            }
        }

        return null;
    }

    public Sprite getMtlSprite(Identifier name) {
        return this.textureGetter.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, name));
    }

    public void setTextureGetter(Function<SpriteIdentifier, Sprite> textureGetter) {
        this.textureGetter = textureGetter;
    }

    public SpriteIdentifier getSprite() {
        return this.sprite;
    }
}
