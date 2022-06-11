package menu.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import menu.swing.Button;
import javax.swing.JComponent;
import javax.swing.JLabel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

public class Menu extends JComponent {

    private int autoIndex;
    private Animator animator;
    private Rectangle rectangle;
    private TimingTarget target;
    private int selectedIndex = -1;
    private List<EventMenuSelected> events = new ArrayList<>();
    
    public Menu() {
        setLayout(new MigLayout("fillx, wrap, inset 0, top","fill", "[70]50[]0[]"));   
        Icon icon = new ImageIcon(getClass().getResource("/menu/icons/add-free-icon-font.png"));
        add(new JLabel(new ImageIcon(getClass().getResource("/menu/icons/archive.png"))));
        addMenu("Item", icon);
        addMenu("Item", icon);
        addMenu("Item", icon);
        addMenu("Item", icon);
        animator = new Animator(500);
        animator.setAcceleration(.5f);
        animator.setDeceleration(.5f);
        animator.setResolution(0);
       
    }
    
    private void addMenu(String item, Icon icon){
        add(createMenu(item, icon), "h 70");
    }
    
    private Component createMenu(String menuName, Icon icon){
        MenuItem item = new MenuItem(icon, autoIndex++);
        item.setMenuColor(new Color(220, 220, 220));
        item.setVerticalTextPosition(JButton.BOTTOM);
        item.setHorizontalTextPosition(JButton.CENTER);
        item.setText(menuName);
        item.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedIndex(item.getIndex());
            }
        });
        return item;
    }
    
    public void setSelectedIndex(int index){
        runEvent(index);
        if(selectedIndex != -1){
            if(animator.isRunning()){
                animator.stop();
            }
            animator.removeTarget(target);
            target = new PropertySetter(this, "animate", rectangle, getMenuAt(index).getBounds());
            animator.addTarget(target);
            selectedIndex = index;
            animator.start();
            repaint();
        }else {
            rectangle = getMenuAt(index).getBounds();
            selectedIndex = index;
            repaint();
        }
    }
    
    private MenuItem getMenuAt(int index){
        for (Component com: getComponents()) {
            if(com instanceof MenuItem){
                MenuItem item = (MenuItem) com;
                if(item.getIndex() == index){
                    return item;
                }
            }
        }
        return null;
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        if(selectedIndex >= 0){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .4f));
            g2.setColor(new Color(255, 255, 255));
            g2.fill(rectangle);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .7f));
            g2.setColor((Color.WHITE));
            g2.fillRect(rectangle.x, rectangle.y, 2, rectangle.height);
            g2.dispose();
        }
        super.paintComponent(g);
    }
    
    public void setAnimate(Rectangle rec){
        this.rectangle = rec;
        repaint();
    }
    
    public void runEvent(int index){
        for(EventMenuSelected event: events){
            event.selected(index);
        }
    }
    
    public void addEventSelected(EventMenuSelected event){
        events.add(event);
    }

    private class MenuItem extends Button {

        private final Icon icon;
        private final int index;

        public MenuItem(Icon icon, int index) {
            this.icon = icon;
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setMenuColor(Color color) {
            setForeground(color);
            setIcon(icon);
        }

    }

}
