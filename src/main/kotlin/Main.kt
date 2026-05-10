import com.formdev.flatlaf.themes.FlatMacDarkLaf
import java.awt.Font
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import kotlin.concurrent.timer

/**
 * Scaling function
 *
 * scales an image to properly fit its container
 */
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
 * Background class
 *
 * purely informational, stores the location and any clickables, if none then defaults to an unclickable area
 */
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

    init {
        val keyBoardBackgrounds = listOf(Background("images/keyBoardWindow.png", 363,312,51,72),Background("images/keyBoardWindow2.png"))
        val startBackgrounds = listOf(Background("images/startWindow.png"))
        val breakerBackgrounds = listOf(Background("images/breakerWindow.png",228,83,268,314),Background("images/breakerWindow2.png",407,148,38,193),Background("images/breakerWindow3.png"))
        val officeBackgrounds = listOf(Background("images/officeWindow.png"),Background("images/officeWindow2.png",345,199,52,198),Background("images/officeWindow3.png"))
        val empty1Backgrounds = listOf(Background("images/empty1Window.png"))
        val supplyClosetBackgrounds = listOf(Background("images/supplyClosetWindow.png",180,120,360,360),Background("images/supplyClosetWindow2.png",180,120,360,360),Background("images/supplyClosetWindow3.png"))
        val loseScreenBackgrounds = listOf(Background("images/loseWindow.png"))
        val chainsawBackgrounds = listOf(Background("images/chainsawWindow.png",295,205,98,99), Background("images/chainsawWindow2.png"))

        val keyBoard = Location("keyBoard", keyBoardBackgrounds, "images/keyBoardButton.png", Point(141,113))
        val start = Location("Start", startBackgrounds, "images/startButton.png", Point(237, 282))
        val breaker = Location("Breaker", breakerBackgrounds, "images/breakerButton.png", Point(245, 37))
        val office = Location("Office", officeBackgrounds,"images/officeButton.png",  Point(44, 276))
        val empty1 = Location("Empty1", empty1Backgrounds, "images/empty1Button.png", Point(122, 205))
        val supplyCloset = Location("Supply Closet", supplyClosetBackgrounds, "images/supplyClosetButton.png", Point(38, 35))
        val loseScreen = Location("Lose Screen", loseScreenBackgrounds, "images/startButton.png", Point(150, 150))
        val chainsaw = Location("Chainsaw", chainsawBackgrounds, "images/chainsawButton.png", Point(304, 179))

        locationList.add(start)
        locationList.add(breaker)
        locationList.add(empty1)
        locationList.add(supplyCloset)
        locationList.add(office)
        locationList.add(loseScreen)
        locationList.add(keyBoard)
        locationList.add(chainsaw)
    }

    var currentLocation: Location = locationList[0]

    var innerMonologue: String = "Arright, let's kill this human!" // the text that gives basic hints and clues

    private val wireCutters = Item("Wire Cutters")
    private val supplyKey = Item("Supply Closet Key")
    private val chainsaw = Item("Chainsaw")

    /**
     * nextBackground function changes the background depending on how the player interacts with it
     */
    fun nextBackground() {
        if (currentLocation == locationList[3] && currentLocation.currentBackgroundIndex == 1) {
            inventory.add(wireCutters)
            innerMonologue = "Hey look, something sharp and snippy!"
            currentLocation.currentBackgroundIndex++
        }
        if (currentLocation == locationList[1] && currentLocation.currentBackgroundIndex == 1 && inventory.contains(wireCutters)) {
            inventory.remove(wireCutters)
            innerMonologue = "Wow these wire cutters are flimsy. Broke after one snip."
            currentLocation.currentBackgroundIndex++
            locationList[4].currentBackgroundIndex++
        }
        if (currentLocation == locationList[1] && currentLocation.currentBackgroundIndex == 1 && !inventory.contains(wireCutters)) {
            innerMonologue = "I need something sharp and snippy to cut this singular blue wire."
        }
        if (currentLocation == locationList[3] && currentLocation.currentBackgroundIndex == 0 && inventory.contains(supplyKey)) {
            currentLocation.currentBackgroundIndex++
            innerMonologue = "So this is the supply closet this thing goes into"
        }
        if (currentLocation == locationList[3] && currentLocation.currentBackgroundIndex == 0 && !inventory.contains(supplyKey)) {
            innerMonologue = "I think i need a generic supply closet key"
        }
        if (currentLocation == locationList[1] && currentLocation.currentBackgroundIndex == 0) {
            currentLocation.currentBackgroundIndex++
        }
        if (currentLocation == locationList[4] && currentLocation.currentBackgroundIndex == 1 && !inventory.contains(chainsaw)) {
            innerMonologue = "I dont wanna touch this goody two shoes, I need a murder weapon..."
        }
        if (currentLocation == locationList[4] && currentLocation.currentBackgroundIndex == 1 && inventory.contains(chainsaw)) {
            currentLocation.currentBackgroundIndex++
        }
        if (currentLocation == locationList[6] && currentLocation.currentBackgroundIndex == 0) {
            currentLocation.currentBackgroundIndex++
            inventory.add(supplyKey)
            innerMonologue = "A lone key... I wonder which supply closet this thing goes into"
        }
        if (currentLocation == locationList[7] && currentLocation.currentBackgroundIndex == 0) {
            currentLocation.currentBackgroundIndex++
            inventory.add(chainsaw)
            innerMonologue = "I could really do some human damage with this..."
        }
    }
}

/**
 * Item class
 *
 * a simple class used to hold 'keys' to progress through the game
 */
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
    var currentBackgroundIndex: Int = 0 // Each location has different backgrounds, and this keeps track of which one to show
}


/**
 * Main UI window, handles user clicks, etc.
 *
 * @param app the app state object
 */
class MainWindow(val app: App) {
    val frame = JFrame("Kill the Human")

    // variables for time pressure aspect of game
    private val timer = Timer(1000,null)
    private var countDown = 60

    private val panel = JLayeredPane().apply { layout = null }
    private var imageLabel = JLabel()
    private var countDownLabel = JLabel("$countDown")

    // other windows
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
        timer.start()
    }

    /**
     * sets up the main window
     */
    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(720, 480)

        imageLabel.setBounds(0, 0, 720, 480)
        countDownLabel.setBounds(10,10,100,100)

        panel.add(imageLabel)
        panel.add(countDownLabel)
        panel.setLayer(countDownLabel, JLayeredPane.DEFAULT_LAYER+1)
    }

    /**
     * sets up the styles
     */
    private fun setupStyles() {
        countDownLabel.font = Font("Arial", Font.PLAIN, 50)
    }

    /**
     * sets up the window
     */
    private fun setupWindow() {
        frame.isResizable = false                           // Can't resize
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE  // Exit upon window close
        frame.contentPane = panel                           // Define the main content
        frame.pack()
        frame.setLocationRelativeTo(null)                   // Centre on the screen
    }

    /**
     * sets up the actions
     */
    private fun setupActions() {
        // This checks to see where the mouse is clicking
        imageLabel.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                handleBackgroundClick(e.x,e.y)
            }

        })

        timer.addActionListener { handleTimerEnd() }
    }

    /**
     * ticks the countdown along whenever the timer ends, or every second. lose game is countdown hits 0
     */
    private fun handleTimerEnd() {
        countDown--
        updateUI()
        if (countDown == 0) {
            imageLabel.icon = ImageIcon(ClassLoader.getSystemResource("images/loseScreen.png"))
            timer.stop()
            mapWindow.die() // closes the map window to prevent movement
        }
    }

    /**
     * checks if a mouse click is in a set interactable area
     */
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

    /**
     * updates the UI whenever an input is made, or the countdown goes down
     */
    fun updateUI() {
        val currentLocation = app.currentLocation
        val currentBackground = currentLocation.backgroundList[currentLocation.currentBackgroundIndex]
        val helpme = ClassLoader.getSystemResource(currentBackground.image)
        imageLabel.icon = ImageIcon(helpme)

        countDownLabel.text = "$countDown"

        if (app.currentLocation == app.locationList[4] && app.currentLocation.currentBackgroundIndex == 2) {
            timer.stop()
        }

        mapWindow.updateUI()       // Keep child dialog window UI up-to-date too
        textWindow.updateUI()
    }

    /**
     * shows the frame
     */
    fun show() {
        frame.isVisible = true
    }
}


//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA just a barrier AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

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
    private val map = ImageIcon(ClassLoader.getSystemResource("images/map.png"))
    private val playerIcon = ImageIcon(ClassLoader.getSystemResource("images/playerCharacter.png")).scaled(40,40)
    private val breakerIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[1].button)).scaled(72, 48)
    private val empty1Icon = ImageIcon(ClassLoader.getSystemResource(app.locationList[2].button)).scaled(72, 48)
    private val supplyClosetIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[3].button)).scaled(72, 48)
    private val officeIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[4].button)).scaled(72, 48)
    private val keyBoardIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[6].button)).scaled(72,48)
    private val startIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[0].button)).scaled(72,48)
    private val chainsawIcon = ImageIcon(ClassLoader.getSystemResource(app.locationList[7].button)).scaled(72,48)

    private val mapPanel = JLabel(map)
    private val breakerPanel = JLabel(breakerIcon)
    private val officePanel = JLabel(officeIcon)
    private val empty1Panel = JLabel(empty1Icon)
    private val supplyClosetPanel = JLabel(supplyClosetIcon)
    private val player = JLabel(playerIcon)
    private val keyBoardPanel = JLabel(keyBoardIcon)
    private val startPanel= JLabel(startIcon)
    private val chainsawPanel = JLabel(chainsawIcon)

    init {
        setupLayout()
        setupActions()
        setupWindow()
        updateUI()
    }

    private fun setupLayout() {
        panel.preferredSize = java.awt.Dimension(360, 360)

        mapPanel.setBounds(0,0,360,360)
        breakerPanel.setBounds(211,21, 108, 72)
        officePanel.setBounds(10, 260, 108, 72)
        empty1Panel.setBounds(88, 189,  108, 72)
        supplyClosetPanel.setBounds(4, 19, 108, 72)
        keyBoardPanel.setBounds(107,97,108,72)
        startPanel.setBounds(203,266,108,72)
        chainsawPanel.setBounds(270, 163, 108, 72)

        player.setBounds(238, 283, 40, 40)


        panel.add(player)
        panel.add(officePanel)
        panel.add(empty1Panel)
        panel.add(supplyClosetPanel)
        panel.add(breakerPanel)
        panel.add(keyBoardPanel)
        panel.add(startPanel)
        panel.add(chainsawPanel)
        panel.add(mapPanel, JLayeredPane.DEFAULT_LAYER-1)
    }

    private fun setupWindow() {
        dialog.isResizable = false                              // Can't resize
        dialog.defaultCloseOperation = JDialog.HIDE_ON_CLOSE    // Hide upon window close
        dialog.contentPane = panel                              // Main content panel
        dialog.pack()
    }

    /**
     * these actions check if a panel has been clicked
     */
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

        keyBoardPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                handleKeyBoardClick()
            }
        })

        startPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                handleStartClick()
            }
        })

        chainsawPanel.addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                handleChainsawClick()
            }
        })
    }

    /**
     * the next 7 functions run when their respective panels are clicked, and change your location
     * they should only work if the current location is adjacent via the path on the map
     */
    private fun handleBreakerClick() {
        if (app.currentLocation == app.locationList[7] || app.currentLocation == app.locationList[3]) {
            app.currentLocation = app.locationList[1]
            owner.updateUI()
        }
    }

    private fun handleEmpty1Click() {
        if (app.currentLocation == app.locationList[3] || app.currentLocation == app.locationList[0] || app.currentLocation == app.locationList[2] || app.currentLocation == app.locationList[4] || app.currentLocation == app.locationList[5] || app.currentLocation == app.locationList[6]) {
            app.currentLocation = app.locationList[2]
            owner.updateUI()
        }
    }

    private fun handleSupplyClosetClick() {
        if (app.currentLocation == app.locationList[2] || app.currentLocation == app.locationList[1]) {
            app.currentLocation = app.locationList[3]
            owner.updateUI()
        }
    }

    private fun handleOfficeClick() {
        if (app.currentLocation == app.locationList[2]) {
            app.currentLocation = app.locationList[4]
            owner.updateUI()
        }
    }

    private fun handleKeyBoardClick() {
        if (app.currentLocation == app.locationList[7] || app.currentLocation == app.locationList[2]) {
            app.currentLocation = app.locationList[6]
            owner.updateUI()
        }
    }

    private fun handleStartClick() {
        if (app.currentLocation == app.locationList[2]) {
            app.currentLocation = app.locationList[0]
            owner.updateUI()
        }
    }

    private fun handleChainsawClick() {
        if (app.currentLocation == app.locationList[1] || app.currentLocation == app.locationList[6]) {
            app.currentLocation = app.locationList[7]
            owner.updateUI()
        }
    }

    /**
     * updates the window to move the player to its respective location panel
     */
    fun updateUI() {
        player.setLocation(app.currentLocation.mapLocation.x, app.currentLocation.mapLocation.y) // Use app properties to display state
    }

    /**
     * spawns in the window, offset from the main window
     */
    fun show() {
        val ownerBounds = owner.frame.bounds          // get location of the main window
        dialog.setLocation(                           // Position next to main window
            ownerBounds.x + ownerBounds.width + 10,
            ownerBounds.y - 20
        )

        dialog.isVisible = true
    }

    /**
     * closes the window to prevent movement when game is lost
     */
    fun die() {
        panel.isVisible = false
    }
}

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA just another barrier AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

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

    /**
     * spawns in the window, offset from the main window
     */
    fun show() {
        val ownerBounds = owner.frame.bounds          // get location of the main window
        dialog.setLocation(                           // Position next to main window
            ownerBounds.x + ownerBounds.width + 10,
            ownerBounds.y + 390
        )

        dialog.isVisible = true
    }

    /**
     * changes the text
     */
    fun updateUI() {
        innerMonologue.text = app.innerMonologue
    }
}