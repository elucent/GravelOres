from typing import Dict, List, Optional

def _makeModCondition(mod: str) -> Dict:
    return { "type": "neoforge:mod_loaded", "modid": mod }

def addConditions(data: Dict, mods: Optional[List[str]] = None) -> None:
    """Adds the mod conditions to the given resource"""
    if mods is not None:
        assert len(mods) > 0
        if len(mods) == 1:
            data["neoforge:conditions"] = [_makeModCondition(mods[0])]
        else:
            data["neoforge:conditions"] = [{
                "type": "neoforge:or",
                "values": [ _makeModCondition(mod) for mod in mods ]
            }]