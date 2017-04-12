#Tree Structures in Association Rule Mining

###Compiling and Running

    To run this code, compile from the root as per normal command-line
    java compilation (javac), then use the 'java' command on the project
    root to run. Alternatively, load the project into any IDE such as Eclipse
    (import) and run as you would any other program.
    
###Structure of the Code
    
    DataSource
        A package with classes and functions specifically designed for handling
        and optimizing the processing of data. Key here is the DataSet, which
        itself provides a multitude of views and structures for us to 
        manipulate the data with.
    
    Demo
        Main package, which hosts the demo class. To run the program, run the
        Main.java class.
    
    PTree
        Package with all P-Tree-related objects. The PTree is the most
        important class here, but all of the objects are aggregated by the
        PTree, and filter down specialized functionalities for the PTree nodes.
        Specifically, we differentiate between the PTreeNodeTop and the
        PTreeNodeInternal, which store slightly different types of information
        with regards to the entries/rows in the data set.
    
    TTree
        The core rule-generation-compatible structure of our program. The TTree
        is comprised of TTreeNodes which store supports, and whose respective
        integer-mapped values signify their position in arrays based on their
        level and the path to the node.