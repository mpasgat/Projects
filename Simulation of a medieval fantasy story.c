#include <iostream>
#include <utility>
#include <vector>
#include <map>
#include <memory>
#include <algorithm>
#include <sstream>


// Forward declaration of Character class
class Character;


// Base class for physical items
class PhysicalItem {
protected:
    std::string name;
    std::shared_ptr<Character> owner;

public:
    // constructor where name will assign the name of a specific item and shared pointer will keep the owner
    PhysicalItem(std::string _name, std::shared_ptr<Character> _owner) : name(std::move(_name)), owner(std::move(_owner)) {}

    // deconstructor destroys PhysicalItem
    virtual ~PhysicalItem() = default;

    // returns me the name of an item
    std::string getName() const {
        return name;
    }

    //returns shred pointer of owner
    std::shared_ptr<Character> getOwner() const {
        return owner;
    }


    // four virtual functions
    //gives description
    virtual std::string description() const = 0;

    //gives allowed targets
    virtual std::vector<std::string> getAllowedTargets() const {
        return {};
    }

    // hives the heal value
    virtual int getHealValue() const {
        return 0;
    }

    // gives the damage value
    virtual int getDamageValue() const {
        return 0;
    }
};


// Derived class representing a weapon
class Weapon : public PhysicalItem {
    int damageValue;

public:
    // constructor where name_ will assign the name of a specific weapon and shared pointer will keep the _owner
    // damageValue will represent the damage that gives weapon
    Weapon(std::string _name, std::shared_ptr<Character> _owner, int _damageValue) :
            PhysicalItem(std::move(_name), std::move(_owner)), damageValue(_damageValue) {}



    // returns weapon name and its damage value
    std::string description() const override {
        return name + ":" + std::to_string(damageValue);
    }

    // returns only weapon damage value
    int getDamageValue() const override {
        return damageValue;
    }
};


// Derived class representing a potion
class Potion : public PhysicalItem {
private:
    int healValue;

public:
    // constructor where name_ will assign the name of a specific potion and shared pointer will keep the _owner
    // heal value will represent the heal points that gives potion
    Potion(std::string _name, std::shared_ptr<Character> _owner, int _healValue) :
            PhysicalItem(std::move(_name), std::move(_owner)), healValue(_healValue) {}


    // returns name of potion and its heal value
    std::string description() const override {
        return name + ":" + std::to_string(healValue);
    }

    // returns only heal points
    int getHealValue() const override {
        return healValue;
    }
};


// Derived class representing a spell sheet
class SpellSheet : public PhysicalItem {
private:
    std::vector<std::string> allowedTargets;

public:
    // constructor where name_ will assign the name of a specific SpellSheet and shared pointer will keep the _owner
    // allowed targets will represent the certain amount of names
    SpellSheet(std::string _name, std::shared_ptr<Character> _owner, std::vector<std::string> _allowedTargets) :
            PhysicalItem(std::move(_name), std::move(_owner)), allowedTargets(std::move(_allowedTargets)) {}


    // returns names of certain spellSheet
    std::vector<std::string> getAllowedTargets() const override {
        return allowedTargets;
    }

    // returns name and number of allowed targets
    std::string description() const override {
        return name + ":" + std::to_string(allowedTargets.size());
    }
};


// Template specialization for Container class
template <typename T>
class Container {

public:
    // deconstructor destroys base container
    virtual ~Container() = default;

    // add item that adds shared pointer of certain item
    virtual bool addItem(std::shared_ptr<PhysicalItem> item) = 0;

    // remove item erases certain item
    virtual bool removeItem(const std::string& itemName) = 0;


    // shows me certain item
    virtual std::string showItems() const = 0;

};


// Concept for checking if a type is derived from PhysicalItem
template<typename T> concept DerivedFromPhysicalItem = std::is_base_of<PhysicalItem, T>::value;

// Template class representing a container for physical items
template<DerivedFromPhysicalItem  T>
class Container<T> {
public:
    // implementation of add item
    bool addItem(std::shared_ptr<T> item)  {
        if (auto typedItem = std::dynamic_pointer_cast<T>(item)) {
            items[typedItem->getName()] = typedItem;
            return true;
        }
        return false;
    }

    // implementation of remove item
    bool removeItem(const std::string& itemName) {
        auto it = items.find(itemName);
        if (it != items.end()) {
            items.erase(it);
            return true;
        }
        return false;
    }

    // implementation of show item
    std::string showItems() const  {
        std::stringstream ss;

        for (const auto& pair : items) {
            ss << pair.second->description() << " ";
        }
        return ss.str();
    }

    // map items where items will keep key string and value shared pointer
    std::map<std::string, std::shared_ptr<T>> items;
};


// Base class representing a character
class Character {
private:
    std::string name;
    int healthPoints;

public:
    // Constructor where his or her name, health points, weapons, potions, and spellSheets will be assigned
    Character(std::string _name, int _healthPoints) :
            name(std::move(_name)), healthPoints(_healthPoints),
            weapons(std::make_shared<Container<Weapon>>()),
            potions(std::make_shared<Container<Potion>>()),
            spellSheets(std::make_shared<Container<SpellSheet>>()) {}


    // deconstructor destroys base Character
    virtual ~Character() = default;

    // returns name of character
    std::string getName() const {
        return name;
    }

    // returns health points of character
     int getHealthPoints() const {
        return healthPoints;
    }

    // says if character alive
     bool isAlive() const {
        return healthPoints > 0;
    }


    // adds item certain
    bool addItem(const std::shared_ptr<PhysicalItem>& item) {
        if (auto weapon = std::dynamic_pointer_cast<Weapon>(item)) {
            return weapons->addItem(weapon);
        } else if (auto potion = std::dynamic_pointer_cast<Potion>(item)) {
            return potions->addItem(potion);
        } else if (auto spell = std::dynamic_pointer_cast<SpellSheet>(item)) {
            return spellSheets->addItem(spell);
        }
        return false;
    }

    // Functions to show character's inventory
    std::string showWeapons() const {
        return weapons->showItems();
    }

    std::string showPotions() const {
        return potions->showItems();
    }

    std::string showSpells() const {
        return spellSheets->showItems();
    }


    // character receives damage and if it is less or equal to 0 he or she dies with her or his items
    void receiveDamage(int damage) {
        healthPoints -= damage;
        if (healthPoints <= 0) {
            healthPoints = 0;
            weapons.reset();
            potions.reset();
            spellSheets.reset();
        }
    }

    // Virtual functions to get maximum allowed items for different character types
    virtual int getMaxAllowedWeapons() const {
        return 0;
    }

    virtual int getMaxAllowedPotions() const {
        return 0;
    }

    virtual int getMaxAllowedSpells() const {
        return 0;
    }

    virtual const char * getType() {
        return "";
    }



    std::shared_ptr<Container<Weapon>> weapons;
    std::shared_ptr<Container<SpellSheet>> spellSheets;
    std::shared_ptr<Container<Potion>> potions;

    friend class PhysicalItem;
};


// Derived class representing a fighter Character
class Fighter : public Character {
public:
    Fighter(std::string _name, int _healthPoints) : Character(std::move(_name), _healthPoints) {}


    //returns type of fighter
    const char * getType() override{
        return "fighter";
    }

    const int maxAllowedWeapons = 3;

    int getMaxAllowedWeapons() const override {
        return maxAllowedWeapons;
    }

    int getMaxAllowedPotions() const override{
        return maxAllowedPotions;
    }

    const int maxAllowedPotions = 5;
};


// Derived class representing an archer Character
class Archer : public Character {
public:
    Archer(std::string _name, int _healthPoints) : Character(std::move(_name), _healthPoints) {}

    //returns type archer
    const char * getType() override{
        return "archer";
    }
    const int maxAllowedWeapons = 2;
    const int maxAllowedPotions = 3;
    const int maxAllowedSpells = 2;

    int getMaxAllowedWeapons() const override{
        return maxAllowedWeapons;
    }

    int getMaxAllowedPotions() const override {
        return maxAllowedPotions;
    }

    int getMaxAllowedSpells() const override{
        return maxAllowedSpells;
    }
};


// Derived class representing a wizard Character
class Wizard : public Character {
public:
    Wizard(std::string _name, int _healthPoints) : Character(std::move(_name), _healthPoints) {}


    // returns type wizard
    const char * getType() override{
        return "wizard";
    }
    const int maxAllowedPotions = 10;
    const int maxAllowedSpells = 10;

    int getMaxAllowedPotions() const override{
        return maxAllowedPotions;
    }

    int getMaxAllowedSpells() const override{
        return maxAllowedSpells;
    }
};



// Class for managing the game
// here will be functions methods for all possible intances
class GameManager {
private:
    std::map<std::string, std::shared_ptr<Character>> characters;

public:

    // Function to create a new character
    std::string createCharacter(const std::string& type, const std::string& name, int initHP) {
        if (characters.find(name) != characters.end()) {
            return "Error caught\n";
        }
        std::shared_ptr<Character> newCharacter;
        if (type == "fighter") {
            newCharacter = std::make_shared<Fighter>(name, initHP);
        } else if (type == "archer") {
            newCharacter = std::make_shared<Archer>(name, initHP);
        } else if (type == "wizard") {
            newCharacter = std::make_shared<Wizard>(name, initHP);
        } else {
            return "Error caught\n";
        }
        characters[name] = newCharacter;
        return "A new " + type + " came to town, " + name + ".\n";
    }


    // Function to create a new spell
    std::string createSpell(const std::string& ownerName, const std::string& spellName, const std::vector<std::string>& characterNames) {
        if (characters.find(ownerName) == characters.end()) {
            return "Error caught\n";
        }
        std::shared_ptr<Character> owner = characters[ownerName];
        if (owner->spellSheets->items.size()==owner->getMaxAllowedSpells()) {
            return "Error caught\n";
        }

        std::vector<std::string> allowedTargets;
        for (const auto& name : characterNames) {
            if (characters.find(name) != characters.end()) {
                allowedTargets.push_back(name);
            } else {

                return "Error caught\n";
            }
        }


        owner->addItem(std::make_shared<SpellSheet>(spellName, owner, allowedTargets));
        std::string re = ownerName + " just obtained a new spell called " + spellName + ".\n";
        return re;
    }


    // Function to create a new potion or weapon
    std::string createItem(const std::string& itemType, const std::string& ownerName, const std::string& itemName, int value) {
        if (characters.find(ownerName) == characters.end()) {

            return "Error caught\n";
        }

        std::shared_ptr<Character> owner = characters[ownerName];
        std::shared_ptr<Character> character = characters.at(ownerName);

        if (itemType == "weapon") {
            if (value <= 0 || owner->weapons->items.size()==owner->getMaxAllowedWeapons()) {

                return "Error caught\n";
            }
            owner->addItem(std::make_shared<Weapon>(itemName, owner, value));
        } else if (itemType == "potion") {
            if (value <= 0 || owner->potions->items.size()==owner->getMaxAllowedPotions()) {

                return "Error caught\n";
            }
            owner->addItem(std::make_shared<Potion>(itemName, owner, value));
        } else {

            return "Error caught\n";
        }

        std::string re = ownerName + " just obtained a new " + itemType + " called " + itemName + ".\n";
        return re;
    }


    // Function to handle an attack action
    std::string attack(const std::string& attackerName, const std::string& targetName, const std::string& weaponName) {
        if (characters.find(attackerName) == characters.end() || characters.find(targetName) == characters.end()) {

            return "Error caught\n";
        }

        std::shared_ptr<Character> attacker = characters[attackerName];
        std::shared_ptr<Character> target = characters[targetName];


        if (!attacker->isAlive()) {

            return "Error caught\n";
        }

        auto weaponContainer = std::dynamic_pointer_cast<Container<Weapon>>(attacker->weapons);


        auto weapon = weaponContainer->items.find(weaponName);
        if (weapon == weaponContainer->items.end()) {

            return "Error caught\n";
        }

        int damage = weapon->second->getDamageValue();
        characters.find(targetName)->second->receiveDamage(damage);
        std::string re = attackerName + " attacks " + targetName + " with their " + weaponName + "!";


        if (!target->isAlive()) {

            characters.erase(targetName);
            return re+"\n" + targetName + " has died...\n";
        } else return re+"\n";


    }


    // Function to display all characters
    std::string showCharacters() const {
        std::vector<std::string> aliveCharacterNames;
        for (const auto& pair : characters) {
            if (pair.second->isAlive()) {
                aliveCharacterNames.push_back(pair.first);
            }
        }
        std::sort(aliveCharacterNames.begin(), aliveCharacterNames.end());
        std::string re;
        for (const auto& name : aliveCharacterNames) {
            std::shared_ptr<Character> character = characters.at(name);
            re = re+character->getName() + ":" + character->getType() + ":" + std::to_string(character->getHealthPoints()) + " ";

        }
        return re+"\n";

    }


    //function to cast a spell
    std::string castSpell(const std::string& casterName, const std::string& targetName, const std::string& spellName) {
        if (characters.find(casterName) == characters.end() || characters.find(targetName) == characters.end()) {

            return "Error caught\n";
        }

        std::shared_ptr<Character> caster = characters[casterName];
        std::shared_ptr<Character> target = characters[targetName];

        if (!caster->isAlive() || !target->isAlive()) {

            return "Error caught\n";
        }

        auto spellContainer = std::dynamic_pointer_cast<Container<SpellSheet>>(caster->spellSheets);


        auto spell = spellContainer->items.find(spellName);
        if (spell == spellContainer->items.end()) {

            return "Error caught\n";
        }

        auto allowedTargets = spell->second->getAllowedTargets();
        if (std::find(allowedTargets.begin(), allowedTargets.end(), targetName) == allowedTargets.end()) {

            return "Error caught\n";
        }

        target->receiveDamage(target->getHealthPoints()); //
        std::string re = casterName + " casts " + spellName + " on " + targetName + "!";
        spellContainer->removeItem(spellName);

        if (!target->isAlive()) {

            characters.erase(targetName);
            return re+"\n"+targetName+" has died...\n";
        } else return re+"\n";


    }

    // Function to drink potion
    std::string drinkPotion(const std::string& supplierName, const std::string& drinkerName, const std::string& potionName) {
        if (characters.find(supplierName) == characters.end() || characters.find(drinkerName) == characters.end()) {

            return "Error caught\n";
        }

        std::shared_ptr<Character> supplier = characters[supplierName];
        std::shared_ptr<Character> drinker = characters[drinkerName];


        if (!supplier->isAlive() || !drinker->isAlive()) {

            return"Error caught\n";
        }

        auto potionContainer = std::dynamic_pointer_cast<Container<Potion>>(supplier->potions);


        auto potion = potionContainer->items.find(potionName);
        if (potion == potionContainer->items.end()) {

            return "Error caught\n";
        }

        int healValue = potion->second->getHealValue();
        drinker->receiveDamage(-healValue); // Heal the drinker

        potionContainer->removeItem(potionName);

        return drinkerName + " drinks " + potionName + " from " + supplierName + ".\n";
    }


    // Function to display speech
    std::string dialogue(const std::string& speaker, int speechLength, const std::vector<std::string>& speech) {
        if (characters.find(speaker) == characters.end() && speaker != "Narrator") {

            return "Error caught\n";
        }

        std::string speakerName = (speaker == "Narrator") ? "Narrator" : characters[speaker]->getName();
        std::string re;
        re = speakerName + ":";
        for (const auto &word: speech) {
            re = re + " " + word;
        }

        return re+"\n";
    }

    // Function to display weapons of a character
    std::string showWeapons(const std::string& characterName) const {

        if (characters.find(characterName) == characters.end()) {

            return "Error caught\n";
        }

        std::shared_ptr<Character> character = characters.at(characterName);
        auto weaponContainer = std::dynamic_pointer_cast<Container<Weapon>>(character->weapons);


        if (!weaponContainer || character->getType()=="wizard") {

            return "Error caught\n";
        }

        return character->showWeapons()+"\n";
    }

    // Function to display potions of a character
    std::string showPotions(const std::string& characterName) const {


        if (characters.find(characterName) == characters.end()) {

            return "Error caught\n";
        }
        std::shared_ptr<Character> character = characters.at(characterName);

        return character->showPotions()+"\n";
    }


    // Function to display spells of a character
    std::string showSpells(const std::string& characterName) const {

        if (characters.find(characterName) == characters.end()) {

            return "Error caught\n";
        }

        std::shared_ptr<Character> character = characters.at(characterName);
        if (character->getType()=="fighter") {
            return "Error caught\n";
        }

        return character->showSpells()+"\n";
    }

};


// Main function
int main() {
    freopen("input.txt", "r", stdin);
    freopen("output.txt", "w", stdout);

    int n;
    std::cin >> n;
    GameManager gameManager;

    // Loop to process game events
    for (int i = 0; i < n; ++i) {
        std::string event;
        std::cin >> event;


        // Handling different types of events
        if (event == "Create") {
            std::string objectType, type, name;
            int initHP;
            std::cin >> objectType;
            if (objectType == "character") {
                std::cin >> type >> name >> initHP;
                std::cout << gameManager.createCharacter(type, name, initHP);
                continue;
            } else if (objectType == "item") {
                std::string itemType, ownerName, itemName;
                int value;
                std::cin >> itemType >> ownerName >> itemName >> value;
                if (itemType == "weapon" || itemType == "potion") {
                    std::cout << gameManager.createItem(itemType, ownerName, itemName, value);
                    continue;
                } else if (itemType == "spell") {
                    std::vector<std::string> characterNames;
                    characterNames.resize(value);
                    for (int j = 0; j < value; ++j) {
                        std::cin >> characterNames[j];
                    }
                    std::cout << gameManager.createSpell(ownerName, itemName, characterNames);
                    continue;
                }
            }
        } else if (event == "Attack") {
            std::string attackerName, targetName, weaponName;
            std::cin >> attackerName >> targetName >> weaponName;
            std::cout << gameManager.attack(attackerName, targetName, weaponName);
            continue;
        } else if (event == "Cast") {
            std::string casterName, targetName, spellName;
            std::cin >> casterName >> targetName >> spellName;
            std::cout << gameManager.castSpell(casterName, targetName, spellName);
            continue;
        } else if (event == "Drink") {
            std::string supplierName, drinkerName, potionName;
            std::cin >> supplierName >> drinkerName >> potionName;
            std::cout << gameManager.drinkPotion(supplierName, drinkerName, potionName);
            continue;
        } else if (event == "Dialogue") {
            std::string speaker;
            int speechLength;
            std::cin >> speaker >> speechLength;
            std::vector<std::string> speech(speechLength);
            for (int j = 0; j < speechLength; ++j) {
                std::cin >> speech[j];
            }
            std::cout << gameManager.dialogue(speaker, speechLength, speech);
            continue;
        } else if (event == "Show") {
            std::string targetType, characterName;
            std::cin >> targetType;
            if (targetType == "characters") {
                std::cout << gameManager.showCharacters();
                continue;
            } else {
                std::cin >> characterName;
                if (targetType == "weapons") {
                    std::cout << gameManager.showWeapons(characterName); //<< std::endl;
                    continue;
                } else if (targetType == "potions") {
                    std::cout << gameManager.showPotions(characterName); //<< std::endl;
                    continue;
                } else if (targetType == "spells") {
                    std::cout << gameManager.showSpells(characterName) ;//<< std::endl;
                    continue;
                }
            }
        }
    }
    fclose(stdin);
    fclose(stdout);
    return 0;
}
