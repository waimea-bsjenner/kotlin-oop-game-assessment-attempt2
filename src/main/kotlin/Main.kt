import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Color
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
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

class Background(
    val image: String,
    val x: Int = -1,
    val y: Int = -1,
    val w: Int = -1,
    val h: Int = -1
)


/**
 * Manage app state
 *
 * @property timer the amount of time to complete the objective
 */
class App {
    val locationList = mutableListOf<Location>()
    private val inventory = mutableListOf<Item>()

    val timer = Timer(1000, null)
    var countDown = 60

    init {
        val startBackgrounds = listOf(Background("images/startWindow.png"))
        val breakerBackgrounds = listOf(Background("images/breakerWindow.png",240,120,240,240),Background("images/breakerWindow2.png",240,120,240,240),Background("images/breakerWindow3.png"))
        val officeBackgrounds = listOf(Background("images/officeWindow.png"),Background("images/officeWindow2.png",300,240,120,240),Background("images/officeWindow3.png"),Background("images/winScreen.png"))
        val empty1Backgrounds = listOf(Background("images/empty1Window.png"))
        val supplyClosetBackgrounds = listOf(Background("images/supplyClosetWindow.png",180,120,360,360),Background("images/supplyClosetWindow2.png",180,120,360,360),Background("images/supplyClosetWindow3.png"))

        val start = Location("Start", startBackgrounds, "images/startButton.png", Point(150, 150))
        val breaker = Location("Breaker", breakerBackgrounds, "images/breakerButton.png", Point(75, 75))
        val office = Location("Office", officeBackgrounds,"images/officeButton.png",  Point(225, 225))
        val empty1 = Location("Empty1", empty1Backgrounds, "images/empty1Button.png", Point(75, 225))
        val supplyCloset = Location("Supply Closet", supplyClosetBackgrounds, "images/supplyClosetButton.png", Point(225, 75))

        locationList.add(start)
        locationList.add(breaker)
        locationList.add(empty1)
        locationList.add(supplyCloset)
        locationList.add(office)

        setupActions()
    }

    var currentLocation: Location = locationList[0]

    var innerMonologue: String = "Arright, let's kill this human!"

    private val wireCutters = Item("WireCutters")

    fun nextBackground() {
        if (currentLocation == locationList[3] && currentLocation.currentBackgroundIndex == 1) {
            inventory.add(wireCutters)
            innerMonologue = "Hey look, something sharp and snippy!"
            currentLocation.currentBackgroundIndex++
        }
        if (currentLocation == locationList[1] && currentLocation.currentBackgroundIndex == 1 && inventory.contains(wireCutters)) {
            inventory.removeFirst()
            innerMonologue = "Wow these things are flimsy. Broke after one snip."
            currentLocation.currentBackgroundIndex++
            locationList[4].currentBackgroundIndex++
        }
        if (currentLocation == locationList[1] && currentLocation.currentBackgroundIndex == 1 && !inventory.contains(wireCutters)) {
            innerMonologue = "I need something sharp and snippy to cut this singular blue wire."
        }
        if (currentLocation == locationList[3] && currentLocation.currentBackgroundIndex == 0) {
            currentLocation.currentBackgroundIndex++
        }
        if (currentLocation == locationList[1] && currentLocation.currentBackgroundIndex == 0) {
            currentLocation.currentBackgroundIndex++
        }
        if (currentLocation == locationList[4] && currentLocation.currentBackgroundIndex == 1) {
            currentLocation.currentBackgroundIndex++
            timer.stop()
        }
    }

    private fun setupActions() {
        timer.addActionListener { handleTimerEnd() }
    }

    private fun handleTimerEnd() {
        countDown--
        println(countDown)
        if (countDown == 0)
            loseGame()
    }

    private fun loseGame() {

    }
}

class Item(
    val name: String
)

/**
 * Location class
 *
 * has different locations with different interactive things player can travel to
 */

class Location(
    val name: String,
    val backgroundList: List<Background>,
    val button: String,
    val mapLocation: Point,
) {
    var currentBackgroundIndex: Int = 0
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
    private var countDown = JLabel("${app.countDown}")
    private val mapWindow = MapWindow(this, app) // Pass app state to dialog too
    private val textWindow = TextWindow(this, app)

    init {
        setupLayout()
        setupStyles()
        setupActions()
        setupWindow()
        updateUI()
        mapWindow.show()
        textWindow.show()
        app.timer.start()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(720, 480)

        imageLabel.setBounds(0, 0, 720, 480)
        countDown.setBounds(10,10,50,50)

        panel.add(imageLabel)
        panel.add(countDown)
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
        imageLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                handleBackgroundClick(e.x,e.y)
            }

        })
    }

    fun handleBackgroundClick(mouseX: Int, mouseY: Int) {
        val currentLocation = app.currentLocation
        val currentBackground = currentLocation.backgroundList[currentLocation.currentBackgroundIndex]
        val left = currentBackground.x
        val right = currentBackground.x + currentBackground.w
        val top = currentBackground.y
        val bottom = currentBackground.y + currentBackground.h

        if (mouseX in left..right && mouseY in top..bottom) {
            app.nextBackground()
            updateUI()
        }
    }


    fun updateUI() {
        val currentLocation = app.currentLocation
        val currentBackground = currentLocation.backgroundList[currentLocation.currentBackgroundIndex]
        val helpme = ClassLoader.getSystemResource(currentBackground.image)
        imageLabel.icon = ImageIcon(helpme)

        countDown.text = "${app.countDown}"
        if (app.currentLocation == app.locationList[4] && app.currentLocation.currentBackgroundIndex == 2) {
            Thread.sleep(1000L)
            app.currentLocation.currentBackgroundIndex++
            updateUI()
        }

        mapWindow.updateUI()       // Keep child dialog window UI up-to-date too
        textWindow.updateUI()
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
        panel.background = Color.BLACK
    }

    private fun setupWindow() {
        dialog.isResizable = false                              // Can't resize
        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE    // Hide upon window close
        dialog.contentPane = panel                              // Main content panel
        dialog.pack()
    }

    private fun setupActions() {
        breakerPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                handleBreakerClick()
            }
        })

        empty1Panel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                handleEmpty1Click()
            }
        })

        supplyClosetPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                handleSupplyClosetClick()
            }
        })

        officePanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                handleOfficeClick()
            }
        })
    }

    private fun handleBreakerClick() {
        app.currentLocation = app.locationList[1]
        owner.updateUI()
    }

    private fun handleEmpty1Click() {
        app.currentLocation = app.locationList[2]
        owner.updateUI()
    }

    private fun handleSupplyClosetClick() {
        app.currentLocation = app.locationList[3]
        owner.updateUI()
    }

    private fun handleOfficeClick() {
        app.currentLocation = app.locationList[4]
        owner.updateUI()
    }

    fun updateUI() {
        player.setLocation(app.currentLocation.mapLocation.x, app.currentLocation.mapLocation.y)// Use app properties to display state
    }

    fun show() {
        val ownerBounds = owner.frame.bounds          // get location of the main window
        dialog.setLocation(                           // Position next to main window
            ownerBounds.x + ownerBounds.width + 10,
            ownerBounds.y - 20
        )

        dialog.isVisible = true
    }
}

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

/**
 * Text window is a child dialog and shows basic clues
 * and gives hints
 *
 * @param owner the parent frame, used to position and layer the dialog correctly
 * @param app the app state object
 */
class TextWindow(private val owner: MainWindow, private val app: App) {
    private val panel = JPanel().apply { layout = null }
    private val dialog = JDialog(owner.frame, "Your Inner Monologue", false)
    private var innerMonologue = JLabel("<html><center>${app.innerMonologue}")

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(360, 110)

        innerMonologue.setBounds(10,0,360,90)

        panel.add(innerMonologue)
    }

    private fun setupWindow() {
        dialog.isResizable = false                              // Can't resize
        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE    // Hide upon window close
        dialog.contentPane = panel                              // Main content panel
        dialog.pack()
    }

    init {
        setupLayout()
        setupWindow()
        show()
    }

    fun show() {
        val ownerBounds = owner.frame.bounds          // get location of the main window
        dialog.setLocation(                           // Position next to main window
            ownerBounds.x + ownerBounds.width + 10,
            ownerBounds.y + 390
        )

        dialog.isVisible = true
    }
    fun updateUI() {
        innerMonologue.text = app.innerMonologue
    }
}