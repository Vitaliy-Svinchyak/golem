const filename = 'default_pose'
const className = 'small_golem'
const fs = require('fs')
console.log(fs.readdirSync('./'))
let content = fs.readFileSync(filename + '.java').toString()

content = content.replace(`import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;`, `import com.e33.client.animation.animator.Animator;
import com.e33.client.animation.animator.ShootyAnimator;
import com.e33.client.detail.modelBox.ModelBoxWithParameters;
import com.e33.entity.ShootyEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;`)

content = content.replace(`public class ${className} extends ModelBase {`, `public class ShootyModel<T extends ShootyEntity> extends ShootyModel<T> {`)
content = content.replace(`public ${className}() {`, `public ShootyModel() {`)
content = content.replace(`render(Entity`, `render(ShootyEntity`)
content = content.split('ModelRenderer').join('RendererModel')
content = content.split('ModelBox').join('ModelBoxWithParameters')
content = content.split('private final').join('protected')

fs.writeFileSync(filename + '2.java', content)
