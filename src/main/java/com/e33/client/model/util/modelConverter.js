const filename = 'small_golem_aimed'
const className = 'small_golem'
const fs = require('fs')
let content = fs.readFileSync(filename + '.java').toString()

content = content.replace(`import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;`, `import com.e33.entity.ShootyEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.ModelBox;`)

content = content.replace(`public class ${className} extends ModelBase {`, `public class ShootyModel<T extends ShootyEntity> extends EntityModel<T> {`)
content = content.replace(`public ${className}() {`, `public ShootyModel() {`)
content = content.replace(`render(Entity`, `render(ShootyEntity`)
content = content.split('ModelRenderer').join('RendererModel')

fs.writeFileSync(filename + '2.java', content)
