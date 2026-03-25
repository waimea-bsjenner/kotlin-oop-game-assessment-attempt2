import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Font
import java.awt.Point
import javax.swing.*

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
    var name = "Kill The Human"
    var timer = 60
    val locationList = mutableListOf<Location>()

    init {
        val start = Location("Start", Color.BLACK, Point(150, 150))
        val breaker = Location("Breaker", Color.GREEN, Point(75, 75))
        val office = Location("Office", Color.RED, Point(225, 225))
        val empty1 = Location("Empty1", Color.BLUE, Point(75, 225))
        val empty2 = Location("Empty2", Color.YELLOW, Point(225, 75))
        locationList.add(breaker)
        locationList.add(empty1)
        locationList.add(empty2)
        locationList.add(office)
        locationList.add(start)
    }


    var currentLocation: Location = locationList[4]
}

/**
 * Location class
 *
 * has different locations with different interactables player can travel to
 */

class Location(
    val name: String,
    val colour: Color,
    val mapLocation: Point
)

/**
 * Main UI window, handles user clicks, etc.
 *
 * @param app the app state object
 */
class MainWindow(val app: App) {
    val frame = JFrame("Kill the Human")
    private val panel = JPanel().apply { layout = null }

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
        panel.background = app.currentLocation.colour

        mapWindow.updateUI()       // Keep child dialog window UI up-to-date too
    }

    fun show() {
        frame.isVisible = true
    }
}


/**
 * Map window is a child dialog and shows the map
 * of the area you play in
 *
 * @param owner the parent frame, used to position and layer the dialog correctly
 * @param app the app state object
 */
class MapWindow(val owner: MainWindow, val app: App) {
    private val dialog = JDialog(owner.frame, "Map", false)
    private val panel = JPanel().apply { layout = null }

    private val breakerPanel = JPanel()
    private val officePanel = JPanel()
    private val empty1Panel = JPanel()
    private val empty2Panel = JPanel()
    private val player = JPanel()
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
        empty2Panel.setBounds(190, 10, 160, 160)
        player.setBounds(160, 160, 40, 40)

        panel.add(player)
        panel.add(officePanel)
        panel.add(empty1Panel)
        panel.add(empty2Panel)
        panel.add(breakerPanel)
    }

    private fun setupStyles() {
        officePanel.background = Color.RED
        empty1Panel.background = Color.BLUE
        empty2Panel.background = Color.YELLOW
        breakerPanel.background = Color.GREEN
        player.background = Color.MAGENTA
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

        empty2Panel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent) {
                handleEmpty2Click()
            }
        })

        officePanel.addMouseListener(object : java.awt.event.MouseAdapter() {
            override fun mousePressed(e: java.awt.event.MouseEvent) {
                handleOfficeClick()
            }
        })
    }

    private fun handleBreakerClick() {
        app.currentLocation = app.locationList[0]
        println(app.currentLocation.name)
        owner.updateUI()
    }

    private fun handleEmpty1Click() {
        app.currentLocation = app.locationList[1]
        println(app.currentLocation.name)
        owner.updateUI()
    }

    private fun handleEmpty2Click() {
        app.currentLocation = app.locationList[2]
        println(app.currentLocation.name)
        owner.updateUI()
    }

    private fun handleOfficeClick() {
        app.currentLocation = app.locationList[3]
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