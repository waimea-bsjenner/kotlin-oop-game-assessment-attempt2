import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Point
import javax.swing.*
import kotlin.concurrent.timer

fun ImageIcon.scaled(width: Int, height: Int): ImageIcon =
    ImageIcon(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH))

/**
 * Application entry point
 */
fun main() {
    FlatMacDarkLaf.setup()          // Initialise the LAF

    val app = App()                 // Get an app state object
    val window = MainWindow(app)    // Spawn the UI, passing in the app state

    SwingUtilities.invokeLater { window.show() }
}


/**
 * Manage app state
 *
 * @property name the user's name
 * @property timer the amount of time to complete the objective
 */
class App {
    private val name = "Kill The Human"
    val locationList = mutableListOf<Location>()
    val inventory = mutableListOf<Item>()

    var currentLocation: Location = locationList[0]

    init {
        val startBackground = listOf("images/startWindow.png")
        val breakerBackground = listOf("images/breakerWindow.png","images/breakerWindow2.png","images/breakerWindow3")
        val officeBackground = listOf("images/officeWindow.png","images/officeWindow2.png","images/officeWindow3")
        val empty1Background = listOf("images/empty1Window.png")
        val supplyClosetBackground = listOf("images/supplyClosetWindow.png","images/supplyClosetWindow2.png","images/supplyClosetWindow3")

        val start = Location("Start", startBackground, "images/startButton.png", Point(150, 150))
        val breaker = Location("Breaker", breakerBackground, "images/breakerButton.png", Point(75, 75))
        val office = Location("Office", officeBackground,"images/officeButton.png",  Point(225, 225))
        val empty1 = Location("Empty1", empty1Background, "images/empty1Button.png", Point(75, 225))
        val supplyCloset = Location("Supply Closet", supplyClosetBackground, "images/supplyClosetButton.png", Point(225, 75))

        locationList.add(start)
        locationList.add(breaker)
        locationList.add(empty1)
        locationList.add(supplyCloset)
        locationList.add(office)
    }
}

class Item(
    val name: String
)

/**
 * Location class
 *
 * has different locations with different interactables player can travel to
 */

class Location(
    val name: String,
    val background: List<String>,
    val button: String,
    val mapLocation: Point,
) {
    var backgroundImage: ImageIcon = ImageIcon(ClassLoader.getSystemResource(background[0]))
}


/**
 * Main UI window, handles user clicks, etc.
 *
 * @param app the app state object
 */
class MainWindow(private val app: App) {
    val frame = JFrame("Kill the Human")
    private val panel = JPanel().apply { layout = null }
    private var imageLabel = JLabel()
    private val mapWindow = MapWindow(this, app)      // Pass app state to dialog too

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
        mapWindow.show()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(720, 480)
        imageLabel.setBounds(0, 0, 720, 480)
        panel.add(imageLabel)
    }

    private fun setupStyles() {

    }

    private fun setupWindow() {
        frame.isResizable = false                           // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit upon window close
        frame.contentPane = panel                           // Define the main content
        frame.pack()
        frame.setLocationRelativeTo(null)                   // Centre on the screen
    }

    private fun setupActions() {

    }

    fun updateUI() {
        val backImage = ImageIcon(ClassLoader.getSystemResource(app.currentLocation.background[0]))
        imageLabel.icon = backImage

        mapWindow.updateUI()       // Keep child dialog window UI up-to-date too
    }

    fun show() {
        frame.isVisible = true
    }
}


//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

/**
 * Map window is a child dialog and shows the map
 * of the area you play in
 *
 * @param owner the parent frame, used to position and layer the dialog correctly
 * @param app the app state object
 */
class MapWindow(private val owner: MainWindow, private val app: App) {
    private val dialog = JDialog(owner.frame, "Map", false)
    private val panel = JPanel().apply { layout = null }
    private val playerIcon = ImageIcon(ClassLoader.getSystemResource("images/playerCharacter.png")).scaled(40,40)
    private val breakerIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[1].button)).scaled(160, 160)
    private val empty1Icon = ImageIcon(ClassLoader.getSystemResource(app.locationList[2].button)).scaled(160, 160)
    private val supplyClosetIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[3].button)).scaled(160, 160)
    private val officeIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[4].button)).scaled(160, 160)

    private val breakerPanel = JLabel(breakerIcon)
    private val officePanel = JLabel(officeIcon)
    private val empty1Panel = JLabel(empty1Icon)
    private val supplyClosetPanel = JLabel(supplyClosetIcon)
    private val player = JLabel(playerIcon)
    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(360, 360)

        breakerPanel.setBounds(10,10, 160, 160)
        officePanel.setBounds(190, 190, 160, 160)
        empty1Panel.setBounds(10, 190,  160, 160)
        supplyClosetPanel.setBounds(190, 10, 160, 160)
        player.setBounds(160, 160, 40, 40)

        panel.add(player)
        panel.add(officePanel)
        panel.add(empty1Panel)
        panel.add(supplyClosetPanel)
        panel.add(breakerPanel)
    }

    private fun setupStyles() {
        officePanel.background = Color.RED
        empty1Panel.background = Color.BLUE
        supplyClosetPanel.background = Color.YELLOW
        breakerPanel.background = Color.GREEN
    }

    private fun setupWindow() {
        dialog.isResizable = false                              // Can't resize
        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE    // Hide upon window close
        dialog.contentPane = panel                              // Main content panel
        dialog.pack()
    }

    private fun movePlayer(x: Int, y: Int) {
        player.setLocation(x, y)
    }

    private fun setupActions() {
        breakerPanel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent) {
                handleBreakerClick()
            }
        })

        empty1Panel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent) {
                handleEmpty1Click()
            }
        })

        supplyClosetPanel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent) {
                handleSupplyClosetClick()
            }
        })

        officePanel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent) {
                handleOfficeClick()
            }
        })
    }

    private fun handleBreakerClick() {
        app.currentLocation = app.locationList[1]
        println(app.currentLocation.name)
        owner.updateUI()
    }

    private fun handleEmpty1Click() {
        app.currentLocation = app.locationList[2]
        println(app.currentLocation.name)
        owner.updateUI()
    }

    private fun handleSupplyClosetClick() {
        app.currentLocation = app.locationList[3]
        println(app.currentLocation.name)
        owner.updateUI()
    }

    private fun handleOfficeClick() {
        app.currentLocation = app.locationList[4]
        println(app.currentLocation.name)
        owner.updateUI()
    }

    fun updateUI() {
        player.setLocation(app.currentLocation.mapLocation.x, app.currentLocation.mapLocation.y)// Use app properties to display state
    }

    fun show() {
        val ownerBounds = owner.frame.bounds          // get location of the main window
        dialog.setLocation(                           // Position next to main window
            ownerBounds.x + ownerBounds.width + 10,
            ownerBounds.y
        )

        dialog.isVisible = true
    }
}