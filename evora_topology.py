"""
Évora Research Framework - Mininet Topology Script
Reproduces the 12-node edge topology from the Évora/Mayan research papers.

Usage:
    sudo mn --custom evora_topology.py --topo evora --controller remote,ip=127.0.0.1
"""

from mininet.topo import Topo

class EvoraTopo(Topo):
    "12-node edge topology for SDN-based Service Composition."

    def build(self):
        # Add 12 switches representing the edge nodes
        # Switches are named n6 to n17 to match the paper's documentation
        nodes = {}
        for i in range(6, 18):
            name = 'n%s' % i
            nodes[name] = self.addSwitch(name)

        # Connect switches as per Figure 2 / Table II in ETT 2018
        # n9 -- n6
        self.addLink(nodes['n9'], nodes['n6'])
        
        # n6 -- n7
        self.addLink(nodes['n6'], nodes['n7'])
        
        # n7 -- n8
        self.addLink(nodes['n7'], nodes['n8'])
        
        # n8 -- n10
        self.addLink(nodes['n8'], nodes['n10'])
        
        # n10 -- n11, n12
        self.addLink(nodes['n10'], nodes['n11'])
        self.addLink(nodes['n10'], nodes['n12'])
        
        # n11 -- n12, n13
        self.addLink(nodes['n11'], nodes['n12'])
        self.addLink(nodes['n11'], nodes['n13'])
        
        # n13 -- n15
        self.addLink(nodes['n13'], nodes['n15'])
        
        # n15 -- n14, n16, n17
        self.addLink(nodes['n15'], nodes['n14'])
        self.addLink(nodes['n15'], nodes['n16'])
        self.addLink(nodes['n15'], nodes['n17'])

topos = { 'evora': ( lambda: EvoraTopo() ) }
